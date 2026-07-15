package io.lackstudio.omnifeed.auth.data.repository

import co.touchlab.kermit.Logger
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import io.lackstudio.omnifeed.auth.utils.AuthManager
import io.lackstudio.omnifeed.auth.utils.DeepLinkBuffer
import io.lackstudio.omnifeed.auth.data.local.source.AuthLocalDataSource
import io.lackstudio.omnifeed.auth.data.remote.source.AuthRemoteDataSource
import io.lackstudio.omnifeed.auth.data.remote.model.dto.UserProfileDto
import io.lackstudio.omnifeed.auth.domain.model.AuthProvider
import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.core.CustomServiceConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import io.ktor.util.encodeBase64
import io.ktor.util.decodeBase64Bytes
import io.ktor.utils.io.core.toByteArray

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
    private val encryptionSalt: String,
    private val customServices: Map<String, CustomServiceConfig> = emptyMap(),
    private val authManager: AuthManager? = null
) : AuthRepository {

    private val logger = Logger.withTag("AuthRepositoryImpl")
    private val manualUser = MutableStateFlow<User?>(null)

    init {
        // Load user from persistent storage
        try {
            val user = localDataSource.getUser()
            manualUser.value = user
            logger.d { "init: manualUser detected from storage: ${user?.id} (Name: ${user?.username})" }
        } catch (e: Exception) {
            logger.e(e) { "init: Failed to load manualUser from storage" }
        }
    }

    private fun saveLocalUser(user: User?) {
        manualUser.value = user
        localDataSource.saveUser(user)
        
        val savedUser = localDataSource.getUser()
        if (user != null) {
            logger.d { "Verification: Saved user in KSafe: ID=${savedUser?.id}, Name=${savedUser?.username}" }
        } else {
            logger.d { "Verification: Saved user in KSafe: ID=${savedUser?.id} (Deleted)" }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUser: Flow<User?> = combine(
        remoteDataSource.authStateChanged,
        manualUser
    ) { sdkUser, manualUser ->
        logger.d { "currentUser combine: sdkUser=${sdkUser?.uid}, manualUser=${manualUser?.id}" }
        val validSdkUser = if (sdkUser != null && sdkUser.uid.isValidUid()) sdkUser else null
        validSdkUser ?: manualUser
    }.flatMapLatest { user ->
        if (user == null) {
            logger.d { "currentUser flatMapLatest: user is null" }
            return@flatMapLatest flowOf(null)
        }
        
        val uid = if (user is FirebaseUser) user.uid else (user as User).id
        if (!uid.isValidUid()) {
            logger.w { "currentUser flatMapLatest: uid is invalid ($uid), skipping Firestore sync" }
            val domainUser = if (user is FirebaseUser) user.toDomain() else user as User
            return@flatMapLatest flowOf(domainUser)
        }
        
        logger.d { "currentUser flatMapLatest: user identified as $uid, listening to Firestore..." }
        
        val serviceFields = customServices.keys.toList()
        
        val profileFlow = if (user is User && user.idToken != null) {
            logger.d { "currentUser: SDK not logged in, using REST to fetch profile for $uid" }
            flow {
                try {
                    emit(remoteDataSource.getUserProfileRest(uid, user.idToken))
                } catch (e: Exception) {
                    logger.e(e) { "currentUser: Failed to fetch profile via REST for $uid" }
                    emit(null)
                }
            }
        } else {
            remoteDataSource.getUserProfile(uid, serviceFields)
        }

        profileFlow.map { profileDto ->
            val domainUser = if (user is FirebaseUser) user.toDomain() else user as User
            
            if (profileDto == null) {
                logger.d { "currentUser: Firestore profile is null, returning basic domain user" }
                return@map domainUser
            }
            
            domainUser.copy(
                linkedServices = profileDto.linkedServices ?: emptyMap(),
                authProviders = profileDto.authProviders?.takeIf { it.isNotEmpty() } ?: domainUser.authProviders,
                username = profileDto.username.takeIf { !it.isNullOrBlank() }
                    ?: domainUser.username,
                email = domainUser.email.takeIf { !it.isNullOrBlank() }
                    ?: profileDto.email,
                photoUrl = domainUser.photoUrl.takeIf { !it.isNullOrBlank() }
                    ?: profileDto.photoUrl.takeIf { !it.isNullOrBlank() },
                // CRITICAL FIX: Explicitly preserve idToken from domainUser
                idToken = domainUser.idToken ?: manualUser.value?.idToken
            )
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): User {
        val firebaseUser = remoteDataSource.signInWithEmail(email, password)
        val domainUser = firebaseUser.toDomain()
        val idToken = domainUser.idToken ?: firebaseUser.getIdToken(false) ?: ""
        val userWithToken = domainUser.copy(idToken = idToken)

        val user = syncProfile(userWithToken, idToken)
        saveUserToFirestore(user)
        saveLocalUser(user)
        return user
    }

    override suspend fun signUpWithEmail(email: String, password: String, username: String?): User {
        val firebaseUser = remoteDataSource.signUpWithEmail(email, password)
        if (username != null) {
            firebaseUser.updateProfile(displayName = username)
        }
        val user = firebaseUser.toDomain().run {
            if (username != null) copy(username = username) else this
        }
        val idToken = user.idToken ?: firebaseUser.getIdToken(false) ?: ""
        val userWithToken = user.copy(idToken = idToken)
        
        saveUserToFirestore(userWithToken)
        saveLocalUser(userWithToken)
        return user
    }

    override suspend fun signInWithGoogle(idToken: String, accessToken: String?): User {
        return try {
            val credential = GoogleAuthProvider.credential(idToken, accessToken)
            val firebaseUser = remoteDataSource.signInWithCredential(credential)
            val domainUser = firebaseUser.toDomain()
            val firebaseIdToken = domainUser.idToken ?: firebaseUser.getIdToken(false) ?: ""
            val userWithToken = domainUser.copy(idToken = firebaseIdToken)

            val user = syncProfile(userWithToken, firebaseIdToken)
            saveUserToFirestore(user)
            saveLocalUser(user)
            user
        } catch (e: Throwable) {
            if (e is NotImplementedError || e.message?.contains("not implemented") == true) {
                signInWithGoogleRest(idToken)
            } else {
                throw e
            }
        }
    }

    override suspend fun signInWithCustomService(serviceName: String, accessToken: String): User {
        val config = customServices[serviceName] ?: throw Exception("Service $serviceName not configured")
        
        // Extract raw token if it's JSON (for Cloud Function verification)
        val rawAccessToken = try {
            if (accessToken.trim().startsWith("{")) {
                Json.parseToJsonElement(accessToken).jsonObject["access_token"]?.jsonPrimitive?.content 
                    ?: Json.parseToJsonElement(accessToken).jsonObject["accessToken"]?.jsonPrimitive?.content
                    ?: accessToken
            } else accessToken
        } catch (_: Exception) { accessToken }

        val customToken = remoteDataSource.fetchFirebaseCustomToken(config.authEndpoint, rawAccessToken, serviceName)
        
        val user = try {
            val firebaseUser = remoteDataSource.signInWithCustomToken(customToken)
            if (!firebaseUser.uid.isValidUid()) {
                remoteDataSource.signInWithCustomTokenRest(customToken, serviceName, rawAccessToken)
            } else {
                val domainUser = firebaseUser.toDomain()
                val idToken = firebaseUser.getIdToken(false) ?: ""
                domainUser.copy(idToken = idToken)
            }
        } catch (_: Exception) {
            remoteDataSource.signInWithCustomTokenRest(customToken, serviceName, rawAccessToken)
        }

        val updatedLinkedServices = user.linkedServices.toMutableMap().apply {
            put(serviceName, true)
        }
        val finalUser = user.copy(linkedServices = updatedLinkedServices)
        
        // CRITICAL FIX: Save to local storage FIRST, then sync to cloud
        localDataSource.saveServiceToken(finalUser.id, serviceName, accessToken)
        
        saveUserToFirestore(finalUser)
        saveLocalUser(finalUser)
        
        updateCustomField(finalUser, serviceName, true)

        return finalUser
    }

    override suspend fun linkWithGoogle(idToken: String, accessToken: String?): User {
        return try {
            val currentUserData = currentUser.first() ?: throw Exception("No user logged in to link with")
            val sdkUser = remoteDataSource.currentUser ?: throw Exception("No user logged in to link with")
            val credential = GoogleAuthProvider.credential(idToken, accessToken)
            val result = sdkUser.linkWithCredential(credential)
            val linkedUser = result.user?.toDomain() ?: throw Exception("Google linking failed: User is null")
            
            val finalUser = linkedUser.copy(
                authProviders = currentUserData.authProviders.toMutableMap().apply { 
                    putAll(linkedUser.authProviders)
                    put(AuthProvider.GOOGLE.id, true) 
                },
                linkedServices = currentUserData.linkedServices // Preserve existing linked services
            )
            saveUserToFirestore(finalUser)
            saveLocalUser(finalUser)
            finalUser
        } catch (e: Throwable) {
            if (e is NotImplementedError || e.message?.contains("not implemented") == true) {
                linkWithGoogleRest(idToken)
            } else {
                throw e
            }
        }
    }

    override suspend fun linkWithCustomService(serviceName: String, accessToken: String): User {
        val user = currentUser.first() ?: throw Exception("No user logged in to link with")
        val config = customServices[serviceName] ?: throw Exception("Service $serviceName not configured")
        
        // Extract raw token if it's JSON (for verification)
        val rawAccessToken = try {
            if (accessToken.trim().startsWith("{")) {
                Json.parseToJsonElement(accessToken).jsonObject["access_token"]?.jsonPrimitive?.content 
                    ?: Json.parseToJsonElement(accessToken).jsonObject["accessToken"]?.jsonPrimitive?.content
                    ?: accessToken
            } else accessToken
        } catch (_: Exception) { accessToken }

        val customToken = remoteDataSource.fetchFirebaseCustomToken(config.authEndpoint, rawAccessToken, serviceName)
        localDataSource.saveServiceToken(user.id, serviceName, accessToken)

        // CRITICAL FIX: If we are already logged in as this custom service user,
        // we MUST re-sign in to refresh the 'auth_time' for sensitive operations like updatePassword.
        val refreshedUser = if (user.id.startsWith("custom:$serviceName")) {
            logger.i { "Refreshing Firebase session for custom service user: ${user.id}" }
            try {
                val firebaseUser = remoteDataSource.signInWithCustomToken(customToken)
                if (!firebaseUser.uid.isValidUid()) {
                    remoteDataSource.signInWithCustomTokenRest(customToken, serviceName, rawAccessToken)
                } else {
                    firebaseUser.toDomain()
                }
            } catch (_: Exception) {
                remoteDataSource.signInWithCustomTokenRest(customToken, serviceName, rawAccessToken)
            }
        } else {
            // Standard linking flow: just refresh profile from REST
            try {
                val idToken = user.idToken ?: remoteDataSource.currentUser?.getIdToken(false)
                if (idToken != null) {
                    remoteDataSource.refreshUserRest(idToken)
                } else user
            } catch (_: Exception) { user }
        }

        updateCustomField(refreshedUser, serviceName, true)
        
        val updatedLinkedServices = refreshedUser.linkedServices.toMutableMap().apply {
            put(serviceName, true)
        }
        val updatedUser = refreshedUser.copy(linkedServices = updatedLinkedServices)
        saveUserToFirestore(updatedUser)
        saveLocalUser(updatedUser)
        return updatedUser
    }

    override suspend fun unlinkCustomService(serviceName: String): User {
        val user = currentUser.first() ?: throw Exception("No user logged in")
        if (!customServices.containsKey(serviceName)) throw Exception("Service $serviceName not configured")
        
        updateCustomField(user, serviceName, false)
        
        localDataSource.clearServiceToken(user.id, serviceName)
        
        val updatedLinkedServices = user.linkedServices.toMutableMap().apply {
            put(serviceName, false)
        }
        val updatedUser = user.copy(linkedServices = updatedLinkedServices)
        saveLocalUser(updatedUser)
        return updatedUser
    }

    override suspend fun linkWithEmail(email: String, password: String): User {
        val sdkUser = remoteDataSource.currentUser ?: throw Exception("No user logged in to link with")
        val credential = EmailAuthProvider.credential(email, password)
        val result = sdkUser.linkWithCredential(credential)
        return result.user?.toDomain() ?: throw Exception("Email linking failed: User is null")
    }

    override suspend fun updatePassword(newPassword: String, oldPassword: String?) {
        val sdkUser = remoteDataSource.currentUser
        val idToken = manualUser.value?.idToken ?: sdkUser?.getIdToken(false)
        
        // RE-AUTH LOGIC: If it's an email user, and they provided an old password, re-verify first.
        // This makes the Token "fresh" and satisfies Firebase's security requirement.
        if (sdkUser != null && !oldPassword.isNullOrBlank() && !sdkUser.email.isNullOrBlank()) {
            try {
                logger.i { "Attempting re-auth with old password for ${sdkUser.email}" }
                val credential = EmailAuthProvider.credential(sdkUser.email!!, oldPassword)
                sdkUser.reauthenticate(credential)
                logger.i { "Re-auth successful" }
            } catch (e: Throwable) {
                // Catch NotImplementedError or "not implemented" message on Desktop/JVM
                if (e is NotImplementedError || e.message?.contains("not implemented") == true) {
                    logger.w { "SDK re-authentication not supported, falling back to sign-in verification..." }
                    // FALLBACK: Use regular sign-in to verify the old password
                    // If this fails (wrong password), it will throw and stop the flow correctly.
                    remoteDataSource.signInWithEmail(sdkUser.email!!, oldPassword)
                } else {
                    logger.e(e) { "Re-auth failed with old password" }
                    throw e 
                }
            }
        }

        logger.d { "updatePassword: using idToken prefix = ${idToken?.take(10)}..." }

        try {
            // Priority 1: Use SDK if available
            if (sdkUser != null) {
                sdkUser.updatePassword(newPassword)
                
                // BUG FIX: Immediately sync to Firestore after SDK password update
                // This ensures "password: true" is reflected in the database right away.
                val domainUser = sdkUser.toDomain()
                val syncedUser = syncProfile(domainUser, idToken ?: "")
                // Force include password provider in case SDK hasn't refreshed providerData yet
                val finalUser = syncedUser.copy(
                    authProviders = syncedUser.authProviders + (AuthProvider.PASSWORD.id to true)
                )
                saveUserToFirestore(finalUser)
                saveLocalUser(finalUser)
            } else if (idToken != null) {
                // Priority 2: Use REST
                performRestPasswordUpdate(idToken, newPassword)
            } else {
                throw Exception("No valid session or Token available to update password")
            }
        } catch (e: Throwable) {
            // Fallback for JVM where sdkUser might exist but updatePassword is not implemented
            if (e is NotImplementedError || e.message?.contains("not implemented") == true) {
                if (idToken != null) {
                    performRestPasswordUpdate(idToken, newPassword)
                } else {
                    throw Exception("SDK does not support password update and no Token available")
                }
            } else {
                throw e
            }
        }
    }

    private suspend fun performRestPasswordUpdate(idToken: String, newPassword: String) {
        val updatedUser = remoteDataSource.updatePasswordRest(idToken, newPassword)
        // Re-sync after update to ensure all providers (google + password) are preserved
        val syncedUser = syncProfile(updatedUser, updatedUser.idToken ?: idToken)
        saveUserToFirestore(syncedUser)
        saveLocalUser(syncedUser)
    }

    override suspend fun updateUsername(username: String): User {
        val sdkUser = remoteDataSource.currentUser
        val currentUserData = manualUser.value ?: currentUser.first() ?: throw Exception("No user logged in")
        val idToken = currentUserData.idToken ?: sdkUser?.getIdToken(false)
        
        val updatedUser = if (sdkUser != null && !(idToken?.contains(".") == true)) {
            sdkUser.updateProfile(displayName = username)
            currentUserData.copy(username = username)
        } else if (idToken != null) {
            remoteDataSource.updateUsernameRest(idToken, username)
        } else {
            currentUserData.copy(username = username)
        }
        
        val syncedUser = syncProfile(updatedUser, updatedUser.idToken ?: idToken ?: "")
        saveUserToFirestore(syncedUser)
        saveLocalUser(syncedUser)
        return syncedUser
    }

    override suspend fun unlinkProvider(providerId: String): User {
        val sdkUser = remoteDataSource.currentUser ?: throw Exception("No user logged in to unlink")
        val resultUser = sdkUser.unlink(providerId)
        return resultUser?.toDomain() ?: throw Exception("Unlinking failed: User is null")
    }

    private suspend fun linkWithGoogleRest(idToken: String): User {
        val currentUserData = currentUser.first() ?: throw Exception("No user logged in")
        val firebaseUser = remoteDataSource.currentUser ?: throw Exception("No user logged in")
        val firebaseIdToken = firebaseUser.getIdToken(false) ?: throw Exception("Failed to get Firebase ID Token")
        val user = remoteDataSource.linkWithGoogleRest(idToken, firebaseIdToken).copy(
            linkedServices = currentUserData.linkedServices // Preserve existing linked services
        )
        saveUserToFirestore(user)
        saveLocalUser(user)
        return user
    }

    private suspend fun signInWithGoogleRest(idToken: String): User {
        val domainUser = remoteDataSource.signInWithGoogleRest(idToken)
        val user = syncProfile(domainUser, domainUser.idToken ?: "")
        saveUserToFirestore(user)
        saveLocalUser(user)
        return user
    }

    private suspend fun syncProfile(domainUser: User, idToken: String): User {
        // Fetch current Firestore profile to preserve existing services/fields
        val profileDto = remoteDataSource.getUserProfileRest(domainUser.id, idToken)
        return if (profileDto != null) {
            domainUser.copy(
                linkedServices = profileDto.linkedServices ?: emptyMap(),
                // FIX: Merge authProviders to avoid losing "google" when logging in via email
                authProviders = (profileDto.authProviders ?: emptyMap()) + domainUser.authProviders,
                username = profileDto.username?.takeIf { it.isNotBlank() } ?: domainUser.username,
                photoUrl = profileDto.photoUrl?.takeIf { it.isNotBlank() } ?: domainUser.photoUrl
            )
        } else domainUser
    }

    override suspend fun signOut() {
        saveLocalUser(null)
        localDataSource.clearAllServiceTokens()
        remoteDataSource.signOut()
        authManager?.signOut()
        DeepLinkBuffer.consumeDeepLink()
    }

    override suspend fun deleteAccount() {
        val sdkUser = remoteDataSource.currentUser
        val manualUserValue = manualUser.value
        val uid = manualUserValue?.id ?: sdkUser?.uid
        val idToken = manualUserValue?.idToken
        
        if (uid.isValidUid()) {
            if (idToken != null) {
                remoteDataSource.deleteUserProfileRest(uid!!, idToken)
            } else {
                remoteDataSource.deleteUserProfile(uid!!)
            }
        }

        var sdkDeleted = false
        if (sdkUser != null) {
            try {
                sdkUser.delete()
                sdkDeleted = true
            } catch (_: Throwable) {
                // SDK delete failed or not implemented, will fallback to REST
            }
        }

        // Only call REST delete if SDK delete was not successful and we have an idToken
        if (!sdkDeleted && idToken != null) {
            remoteDataSource.deleteAccountRest(idToken)
        }

        saveLocalUser(null)
        localDataSource.clearAllServiceTokens()
        remoteDataSource.signOut()
    }

    override suspend fun getServiceToken(serviceName: String): String? {
        val user = currentUser.first() ?: return null
        
        // 1. Try local cache first, fallback to Firestore
        val tokenToUse = localDataSource.getServiceToken(user.id, serviceName)
            ?: try {
                val profileDto = if (user.idToken != null && remoteDataSource.currentUser == null) {
                    // SDK not logged in (e.g. JVM), use REST to fetch profile safely
                    remoteDataSource.getUserProfileRest(user.id, user.idToken!!)
                } else {
                    remoteDataSource.getUserProfile(user.id, listOf("encryptedServiceAuth")).firstOrNull()
                }

                profileDto?.encryptedServiceAuth?.get(serviceName)
                    ?.let { encrypted ->
                        decryptToken(encrypted, user.id).also { decrypted ->
                            localDataSource.saveServiceToken(user.id, serviceName, decrypted)
                        }
                    }
            } catch (e: Exception) {
                logger.e(e) { "Failed to fetch service token from Firestore" }
                null
            }

        // 2. Smart Parsing: If it's a JSON (starts with {), extract accessToken
        return try {
            if (tokenToUse?.trim()?.startsWith("{") == true) {
                val json = Json.parseToJsonElement(tokenToUse).jsonObject
                json["access_token"]?.jsonPrimitive?.content 
                    ?: json["accessToken"]?.jsonPrimitive?.content 
                    ?: tokenToUse
            } else {
                tokenToUse
            }
        } catch (_: Exception) {
            tokenToUse
        }
    }

    private suspend fun updateCustomField(user: User, serviceName: String, isLinked: Boolean) {
        if (user.id.isValidUid()) {
            val idToken = user.idToken
            if (idToken != null) {
                logger.d { "Updating custom field (REST) for ${user.id}: $serviceName = $isLinked" }
                remoteDataSource.updateCustomFieldRest(user.id, idToken, serviceName, isLinked)
            } else {
                logger.d { "Updating custom field (SDK) for ${user.id}: $serviceName = $isLinked" }
                remoteDataSource.updateCustomField(user.id, serviceName, isLinked)
            }
        }
    }

    private suspend fun saveUserToFirestore(user: User) {
        if (user.id.isValidUid()) {
            // Collect all service tokens from local storage to sync to cloud
            val tokensToSync = mutableMapOf<String, String>()
            user.linkedServices.keys.forEach { service ->
                localDataSource.getServiceToken(user.id, service)?.let { token ->
                    tokensToSync[service] = encryptToken(token, user.id)
                }
            }

            val dto = UserProfileDto(
                username = user.username?.takeIf { it.isNotBlank() },
                email = user.email?.takeIf { it.isNotBlank() },
                photoUrl = user.photoUrl?.takeIf { it.isNotBlank() },
                authProviders = user.authProviders,
                linkedServices = user.linkedServices,
                encryptedServiceAuth = tokensToSync.takeIf { it.isNotEmpty() }
            )

            val idToken = user.idToken
            if (idToken != null) {
                logger.d { "Saving user profile to Firestore (REST) for ${user.id}: $dto" }
                remoteDataSource.saveUserProfileRest(user.id, idToken, dto)
            } else {
                logger.d { "Saving user profile to Firestore (SDK) for ${user.id}: $dto" }
                remoteDataSource.saveUserProfile(user.id, dto)
            }
        }
    }

    /**
     * Salted XOR + Base64 obfuscation for cloud sync.
     * This prevents plain-text tokens from appearing in Firestore and handles special characters safely.
     * Recommendation: Upgrade to AES-GCM for production if storing high-value tokens.
     */
    private fun encryptToken(token: String, uid: String): String {
        if (token.isBlank()) return ""
        val key = (uid + encryptionSalt).hashCode()
        val tokenBytes = token.toByteArray() // Convert to UTF-8 bytes first
        
        val xoredBytes = ByteArray(tokenBytes.size) { i ->
            (tokenBytes[i].toInt() xor (key and 0xFF)).toByte()
        }
        
        return xoredBytes.encodeBase64()
    }

    private fun decryptToken(encryptedToken: String, uid: String): String {
        if (encryptedToken.isBlank()) return ""
        return try {
            val decodedBytes = encryptedToken.decodeBase64Bytes()
            val key = (uid + encryptionSalt).hashCode()
            
            val xoredBytes = ByteArray(decodedBytes.size) { i ->
                (decodedBytes[i].toInt() xor (key and 0xFF)).toByte()
            }
            
            xoredBytes.decodeToString() // Convert back from UTF-8 bytes to String
        } catch (e: Exception) {
            logger.e(e) { "Failed to decrypt token" }
            ""
        }
    }

    private fun FirebaseUser.toDomain(): User {
        val providers = try {
            providerData
                .filter { it.providerId != "firebase" }
                .associate {
                    val key = AuthProvider.fromFirebaseId(it.providerId)?.id ?: it.providerId
                    key to true
                }
        } catch (_: Throwable) {
            emptyMap()
        }
        
        // Use manualUser's token as fallback if this FirebaseUser doesn't have it (common on JVM)
        val token = manualUser.value?.idToken
        
        return User(
            id = uid,
            email = email?.takeIf { it.isNotBlank() },
            username = displayName?.takeIf { it.isNotBlank() },
            photoUrl = photoURL?.takeIf { it.isNotBlank() },
            authProviders = providers,
            idToken = token
        )
    }

    private fun String?.isValidUid(): Boolean = !this.isNullOrBlank() && this.length > 5
}
