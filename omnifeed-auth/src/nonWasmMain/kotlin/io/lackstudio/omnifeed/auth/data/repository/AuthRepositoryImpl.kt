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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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
            logger.d { "init: manualUser detected from storage: ${user?.id} (Name: ${user?.displayName})" }
        } catch (e: Exception) {
            logger.e(e) { "init: Failed to load manualUser from storage" }
        }
    }

    private fun saveLocalUser(user: User?) {
        manualUser.value = user
        localDataSource.saveUser(user)
        
        val savedUser = localDataSource.getUser()
        if (user != null) {
            logger.d { "Verification: Saved user in KSafe: ID=${savedUser?.id}, Name=${savedUser?.displayName}" }
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
                emit(remoteDataSource.getUserProfileRest(uid, user.idToken))
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
                displayName = profileDto.displayName.takeIf { !it.isNullOrBlank() }
                    ?: domainUser.displayName,
                email = domainUser.email.takeIf { !it.isNullOrBlank() }
                    ?: profileDto.email,
                photoUrl = domainUser.photoUrl.takeIf { !it.isNullOrBlank() }
                    ?: profileDto.photoUrl.takeIf { !it.isNullOrBlank() }
            )
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val fbUser = remoteDataSource.signInWithEmail(email, password)
            val user = fbUser.toDomain()
            saveUserToFirestore(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String, displayName: String?): Result<User> {
        return try {
            val fbUser = remoteDataSource.signUpWithEmail(email, password)
            if (displayName != null) {
                fbUser.updateProfile(displayName = displayName)
            }
            val user = fbUser.toDomain()
            saveUserToFirestore(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String, accessToken: String?): Result<User> {
        return try {
            val credential = GoogleAuthProvider.credential(idToken, accessToken)
            val fbUser = remoteDataSource.signInWithCredential(credential)
            val user = fbUser.toDomain()
            saveUserToFirestore(user)
            Result.success(user)
        } catch (e: Throwable) {
            if (e is NotImplementedError || e.message?.contains("not implemented") == true) {
                signInWithGoogleRest(idToken)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun signInWithCustomService(serviceName: String, accessToken: String): Result<User> {
        return try {
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
            
            val loginResult = try {
                val fbUser = remoteDataSource.signInWithCustomToken(customToken)
                if (!fbUser.uid.isValidUid()) {
                    Result.success(remoteDataSource.signInWithCustomTokenRest(customToken, serviceName, rawAccessToken))
                } else {
                    Result.success(fbUser.toDomain())
                }
            } catch (_: Exception) {
                Result.success(remoteDataSource.signInWithCustomTokenRest(customToken, serviceName, rawAccessToken))
            }

            loginResult.fold(
                onSuccess = { user ->
                    val updatedLinkedServices = user.linkedServices.toMutableMap().apply {
                        put(serviceName, true)
                    }
                    val finalUser = user.copy(linkedServices = updatedLinkedServices)
                    
                    // CRITICAL FIX: Save to local storage FIRST, then sync to cloud
                    localDataSource.saveServiceToken(finalUser.id, serviceName, accessToken)
                    
                    saveUserToFirestore(finalUser)
                    saveLocalUser(finalUser)
                    
                    updateCustomField(finalUser, serviceName, true)

                    Result.success(finalUser)
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun linkWithGoogle(idToken: String, accessToken: String?): Result<User> {
        return try {
            val sdkUser = remoteDataSource.currentUser ?: throw Exception("No user logged in to link with")
            val credential = GoogleAuthProvider.credential(idToken, accessToken)
            val result = sdkUser.linkWithCredential(credential)
            val linkedUser = result.user?.toDomain() ?: throw Exception("Google linking failed: User is null")
            
            val finalUser = linkedUser.copy(
                authProviders = linkedUser.authProviders.toMutableMap().apply { put(AuthProvider.GOOGLE.id, true) }
            )
            saveUserToFirestore(finalUser)
            saveLocalUser(finalUser)
            Result.success(finalUser)
        } catch (e: Throwable) {
            if (e is NotImplementedError || e.message?.contains("not implemented") == true) {
                linkWithGoogleRest(idToken)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun linkWithCustomService(serviceName: String, accessToken: String): Result<User> {
        return try {
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

            remoteDataSource.fetchFirebaseCustomToken(config.authEndpoint, rawAccessToken, serviceName)
            localDataSource.saveServiceToken(user.id, serviceName, accessToken)

            updateCustomField(user, serviceName, true)
            
            val updatedLinkedServices = user.linkedServices.toMutableMap().apply {
                put(serviceName, true)
            }
            val updatedUser = user.copy(linkedServices = updatedLinkedServices)
            saveUserToFirestore(updatedUser)
            saveLocalUser(updatedUser)
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unlinkCustomService(serviceName: String): Result<User> {
        return try {
            val user = currentUser.first() ?: throw Exception("No user logged in")
            if (!customServices.containsKey(serviceName)) throw Exception("Service $serviceName not configured")
            
            updateCustomField(user, serviceName, false)
            
            localDataSource.clearServiceToken(user.id, serviceName)
            
            val updatedLinkedServices = user.linkedServices.toMutableMap().apply {
                put(serviceName, false)
            }
            val updatedUser = user.copy(linkedServices = updatedLinkedServices)
            saveLocalUser(updatedUser)
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun linkWithEmail(email: String, password: String): Result<User> {
        return try {
            val sdkUser = remoteDataSource.currentUser ?: throw Exception("No user logged in to link with")
            val credential = EmailAuthProvider.credential(email, password)
            val result = sdkUser.linkWithCredential(credential)
            val linkedUser = result.user?.toDomain() ?: throw Exception("Email linking failed: User is null")
            Result.success(linkedUser)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            val sdkUser = remoteDataSource.currentUser ?: throw Exception("No user logged in to update password")
            sdkUser.updatePassword(newPassword)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun unlinkProvider(providerId: String): Result<User> {
        return try {
            val sdkUser = remoteDataSource.currentUser ?: throw Exception("No user logged in to unlink")
            val resultUser = sdkUser.unlink(providerId)
            val unlinkedUser = resultUser?.toDomain() ?: throw Exception("Unlinking failed: User is null")
            Result.success(unlinkedUser)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private suspend fun linkWithGoogleRest(idToken: String): Result<User> {
        return try {
            val fbUser = remoteDataSource.currentUser ?: throw Exception("No user logged in")
            val firebaseIdToken = fbUser.getIdToken(false) ?: throw Exception("Failed to get Firebase ID Token")
            val user = remoteDataSource.linkWithGoogleRest(idToken, firebaseIdToken)
            saveUserToFirestore(user)
            saveLocalUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun signInWithGoogleRest(idToken: String): Result<User> {
        return try {
            val user = remoteDataSource.signInWithGoogleRest(idToken)
            saveUserToFirestore(user)
            saveLocalUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        saveLocalUser(null)
        localDataSource.clearAllServiceTokens()
        remoteDataSource.signOut()
        authManager?.signOut()
        DeepLinkBuffer.consumeDeepLink()
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
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
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
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
                    remoteDataSource.getUserProfile(user.id, listOf("encryptedServiceTokens")).firstOrNull()
                }

                profileDto?.encryptedServiceTokens?.get(serviceName)
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
                displayName = user.displayName.takeIf { !it.isNullOrBlank() },
                email = user.email.takeIf { !it.isNullOrBlank() },
                photoUrl = user.photoUrl.takeIf { !it.isNullOrBlank() },
                authProviders = user.authProviders.takeIf { it.isNotEmpty() },
                linkedServices = user.linkedServices.takeIf { it.isNotEmpty() },
                encryptedServiceTokens = tokensToSync.takeIf { it.isNotEmpty() }
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
                .filter { it.providerId != "firebase" } // Filter out internal Firebase provider ID (common on Android)
                .associate {
                    val key = AuthProvider.fromFirebaseId(it.providerId)?.id ?: it.providerId
                    key to true
                }
        } catch (_: Throwable) { emptyMap() }
        
        return User(
            id = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoURL,
            authProviders = providers
        )
    }

    private fun String?.isValidUid(): Boolean = !this.isNullOrBlank() && this.length > 5
}
