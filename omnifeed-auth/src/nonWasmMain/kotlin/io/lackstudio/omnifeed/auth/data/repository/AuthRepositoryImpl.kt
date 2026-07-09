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

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
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
                displayName = domainUser.displayName.takeIf { !it.isNullOrBlank() }
                    ?: profileDto.displayName,
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
            val customToken = remoteDataSource.fetchFirebaseCustomToken(config.authEndpoint, accessToken, serviceName)
            
            val loginResult = try {
                val fbUser = remoteDataSource.signInWithCustomToken(customToken)
                if (!fbUser.uid.isValidUid()) {
                    Result.success(remoteDataSource.signInWithCustomTokenRest(customToken, serviceName, accessToken))
                } else {
                    Result.success(fbUser.toDomain())
                }
            } catch (_: Exception) {
                Result.success(remoteDataSource.signInWithCustomTokenRest(customToken, serviceName, accessToken))
            }

            loginResult.fold(
                onSuccess = { user ->
                    val updatedLinkedServices = user.linkedServices.toMutableMap().apply {
                        put(serviceName, true)
                    }
                    val finalUser = user.copy(linkedServices = updatedLinkedServices)
                    saveUserToFirestore(finalUser)
                    saveLocalUser(finalUser)
                    
                    updateCustomField(finalUser, serviceName, true)

                    localDataSource.saveServiceToken(finalUser.id, serviceName, accessToken)
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
            
            remoteDataSource.fetchFirebaseCustomToken(config.authEndpoint, accessToken, serviceName)
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
        return localDataSource.getServiceToken(user.id, serviceName)
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
            val dto = UserProfileDto(
                displayName = user.displayName.takeIf { !it.isNullOrBlank() },
                email = user.email.takeIf { !it.isNullOrBlank() },
                photoUrl = user.photoUrl.takeIf { !it.isNullOrBlank() },
                authProviders = user.authProviders.takeIf { it.isNotEmpty() },
                linkedServices = user.linkedServices.takeIf { it.isNotEmpty() }
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

    private fun FirebaseUser.toDomain(): User {
        val providers = try {
            providerData.associate {
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
