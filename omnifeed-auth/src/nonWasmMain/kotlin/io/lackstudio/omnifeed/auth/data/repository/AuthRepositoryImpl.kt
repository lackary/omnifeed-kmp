package io.lackstudio.omnifeed.auth.data.repository

import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.data.model.request.SignInWithIdpRequest
import io.lackstudio.omnifeed.auth.data.model.request.DeleteAccountRequest
import io.lackstudio.omnifeed.auth.data.model.response.SignInWithIdpResponse
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.auth.platform.firebaseApiKey
import io.lackstudio.omnifeed.auth.platform.loadAuthUser
import io.lackstudio.omnifeed.auth.platform.saveAuthUser
import io.lackstudio.omnifeed.core.CustomServiceConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import co.touchlab.kermit.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json


class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val customServices: Map<String, CustomServiceConfig> = emptyMap()
) : AuthRepository {

    private val logger = Logger.withTag("AuthRepositoryImpl")
    private val manualUser = MutableStateFlow<User?>(null)

    init {
        // Load user from persistent storage (used for platforms like JVM with REST fallback)
        manualUser.value = loadAuthUser()
        logger.d { "init: manualUser=${manualUser.value?.id}" }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUser: Flow<User?> = combine(
        firebaseAuth.authStateChanged,
        manualUser
    ) { sdkUser, manualUser ->
        logger.d { "currentUser combine: sdkUser=${sdkUser?.uid}, manualUser=${manualUser?.id}" }
        sdkUser ?: manualUser
    }.flatMapLatest { user ->
        if (user == null) {
            logger.d { "currentUser flatMapLatest: user is null" }
            return@flatMapLatest flowOf(null)
        }
        
        val uid = if (user is FirebaseUser) user.uid else (user as User).id
        logger.d { "currentUser flatMapLatest: user identified as $uid, listening to Firestore..." }
        
        // Listen to Firestore for all configured custom services
        firestore.collection("users").document(uid).snapshots().map { snapshot ->
            val linkedStatus = customServices.mapValues { (_, config) ->
                try {
                    val isLinked = snapshot.get<Boolean>(config.linkedField)
                    logger.v { "Service link status: ${config.linkedField} = $isLinked" }
                    isLinked
                } catch (e: Exception) {
                    false
                }
            }

            if (user is FirebaseUser) {
                user.toDomain().copy(customLinkedServices = linkedStatus)
            } else {
                (user as User).copy(customLinkedServices = linkedStatus)
            }
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password)
            val user = result.user?.toDomain() ?: throw Exception("Login failed: User is null")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String, displayName: String?): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
            val firebaseUser = result.user ?: throw Exception("Registration failed: User is null")
            
            // Update profile with displayName if provided
            if (displayName != null) {
                firebaseUser.updateProfile(displayName = displayName)
            }
            
            Result.success(firebaseUser.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String, accessToken: String?): Result<User> {
        return try {
            val credential = GoogleAuthProvider.credential(idToken, accessToken)
            val result = firebaseAuth.signInWithCredential(credential)
            val user = result.user?.toDomain() ?: throw Exception("Google login failed: User is null")
            Result.success(user)
        } catch (e: Throwable) {
            // Handle cases where the platform SDK doesn't implement certain methods (e.g., JVM)
            if (e is NotImplementedError || e.message?.contains("not implemented") == true) {
                signInWithGoogleRest(idToken)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun signInWithCustomService(serviceName: String, accessToken: String): Result<User> {
        logger.d { "signInWithCustomService: serviceName=$serviceName" }
        return try {
            val config = customServices[serviceName] ?: throw Exception("Service $serviceName not configured")
            
            // 1. Get Firebase Custom Token from your backend, passing the serviceName as provider
            logger.i { "Fetching custom token from ${config.authEndpoint}" }
            val customToken = fetchFirebaseCustomToken(config.authEndpoint, accessToken, serviceName)
            
            // 2. Sign in to Firebase with the custom token
            logger.i { "Signing in with custom token..." }
            val result = firebaseAuth.signInWithCustomToken(customToken)
            val user = result.user?.toDomain() ?: throw Exception("Custom service login failed: User is null")
            
            // 3. Update status in local map
            val updatedLinkedServices = user.customLinkedServices.toMutableMap().apply {
                put(serviceName, true)
            }
            val finalUser = user.copy(customLinkedServices = updatedLinkedServices)
            
            // 4. Update local state and Firestore
            manualUser.value = finalUser
            saveAuthUser(finalUser)
            firestore.collection("users").document(finalUser.id)
                .set(mapOf(config.linkedField to true), merge = true)
            
            logger.i { "signInWithCustomService SUCCESS for user ${finalUser.id}" }
            Result.success(finalUser)
        } catch (e: Exception) {
            logger.e(e) { "signInWithCustomService FAILED" }
            Result.failure(e)
        }
    }

    private suspend fun fetchFirebaseCustomToken(endpoint: String, customAccessToken: String, provider: String): String {
        val httpClient = HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        
        val response = httpClient.post(endpoint) {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "access_token" to customAccessToken,
                "provider" to provider
            ))
        }
        
        if (response.status.value != 200) {
            throw Exception("Failed to fetch custom token: ${response.status}")
        }
        
        val body = response.body<Map<String, String>>()
        return body["custom_token"] ?: throw Exception("No custom token in response")
    }

    override suspend fun linkWithGoogle(idToken: String, accessToken: String?): Result<User> {
        return try {
            val user = firebaseAuth.currentUser ?: throw Exception("No user logged in to link with")
            val credential = GoogleAuthProvider.credential(idToken, accessToken)
            val result = user.linkWithCredential(credential)
            val linkedUser = result.user?.toDomain() ?: throw Exception("Google linking failed: User is null")
            
            // Force set as linked and update manual state if exists
            val finalUser = linkedUser.copy(isGoogleLinked = true)
            manualUser.value = finalUser
            saveAuthUser(finalUser)
            
            Result.success(finalUser)
        } catch (e: Throwable) {
            // Handle cases where the platform SDK doesn't implement certain methods (e.g., JVM)
            if (e is NotImplementedError || e.message?.contains("not implemented") == true) {
                linkWithGoogleRest(idToken)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun linkWithCustomService(serviceName: String, accessToken: String): Result<User> {
        logger.d { "linkWithCustomService: serviceName=$serviceName" }
        return try {
            val user = currentUser.first() ?: throw Exception("No user logged in to link with")
            val config = customServices[serviceName] ?: throw Exception("Service $serviceName not configured")
            
            // 1. Notify backend / Verify token
            logger.i { "Verifying token with backend to trigger linking logic: ${config.authEndpoint}" }
            try {
                // We call the auth endpoint to verify the token and trigger backend logic/logs
                fetchFirebaseCustomToken(config.authEndpoint, accessToken, serviceName)
                logger.d { "Backend verification SUCCESS" }
            } catch (e: Exception) {
                logger.e(e) { "Backend verification FAILED" }
                throw e
            }

            // 2. Update cloud
            logger.d { "Updating Firestore for user: ${user.id}" }
            firestore.collection("users").document(user.id)
                .set(mapOf(config.linkedField to true), merge = true)
            
            // 3. Update local state
            val updatedLinkedServices = user.customLinkedServices.toMutableMap().apply {
                put(serviceName, true)
            }
            val updatedUser = user.copy(customLinkedServices = updatedLinkedServices)
            manualUser.value = updatedUser
            saveAuthUser(updatedUser)

            logger.i { "linkWithCustomService SUCCESS for user ${updatedUser.id}" }
            Result.success(updatedUser)
        } catch (e: Exception) {
            logger.e(e) { "linkWithCustomService FAILED" }
            Result.failure(e)
        }
    }

    override suspend fun unlinkCustomService(serviceName: String): Result<User> {
        return try {
            val user = currentUser.first() ?: throw Exception("No user logged in")
            val config = customServices[serviceName] ?: throw Exception("Service $serviceName not configured")
            
            // 1. Remove link status from cloud
            firestore.collection("users").document(user.id)
                .set(mapOf(config.linkedField to false), merge = true)
            
            // 2. Update local state
            val updatedLinkedServices = user.customLinkedServices.toMutableMap().apply {
                put(serviceName, false)
            }
            val updatedUser = user.copy(customLinkedServices = updatedLinkedServices)
            manualUser.value = updatedUser
            saveAuthUser(updatedUser)
            
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun linkWithEmail(email: String, password: String): Result<User> {
        return try {
            val user = firebaseAuth.currentUser ?: throw Exception("No user logged in to link with")
            val credential = EmailAuthProvider.credential(email, password)
            val result = user.linkWithCredential(credential)
            val linkedUser = result.user?.toDomain() ?: throw Exception("Email linking failed: User is null")
            Result.success(linkedUser)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: throw Exception("No user logged in to update password")
            user.updatePassword(newPassword)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override suspend fun unlinkProvider(providerId: String): Result<User> {
        return try {
            val user = firebaseAuth.currentUser ?: throw Exception("No user logged in to unlink")
            val resultUser = user.unlink(providerId)
            val unlinkedUser = resultUser?.toDomain() ?: throw Exception("Unlinking failed: User is null")
            Result.success(unlinkedUser)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private suspend fun linkWithGoogleRest(idToken: String): Result<User> {
        return try {
            val firebaseUser = firebaseAuth.currentUser ?: throw Exception("No user logged in")
            val firebaseIdToken = firebaseUser.getIdToken(false)
            val apiKey = firebaseApiKey ?: throw Exception("Firebase API Key not found")
            
            val httpClient = HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                    })
                }
            }

            // For linking, we use the same signInWithIdp but provide the current Firebase idToken
            val response = httpClient.post("https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(SignInWithIdpRequest(
                    postBody = "id_token=$idToken&providerId=google.com",
                    idToken = firebaseIdToken, // This identifies the current user for linking
                    requestUri = "http://localhost"
                ))
            }

            if (response.status.value != 200) {
                val errorBody = response.body<String>()
                throw Exception("Firebase REST Linking failed (${response.status}): $errorBody")
            }

            val resultData = response.body<SignInWithIdpResponse>()
            val user = User(
                id = resultData.localId ?: firebaseUser.uid,
                email = resultData.email ?: firebaseUser.email,
                displayName = resultData.displayName ?: firebaseUser.displayName,
                photoUrl = resultData.photoUrl ?: firebaseUser.photoURL,
                isGoogleLinked = true,
                idToken = resultData.idToken
            )

            manualUser.value = user
            saveAuthUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun signInWithGoogleRest(idToken: String): Result<User> {
        return try {
            val apiKey = firebaseApiKey ?: throw Exception("Firebase API Key not found")
            val httpClient = HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true // Ensure default values like requestUri are included in the JSON
                    })
                }
            }

            val response = httpClient.post("https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(SignInWithIdpRequest(
                    postBody = "id_token=$idToken&providerId=google.com"
                ))
            }

            if (response.status.value != 200) {
                val errorBody = response.body<String>()
                throw Exception("Firebase REST failed (${response.status}): $errorBody")
            }

            val resultData = response.body<SignInWithIdpResponse>()

            val user = User(
                id = resultData.localId ?: throw Exception("Login failed: localId is null in response: $resultData"),
                email = resultData.email,
                displayName = resultData.displayName,
                photoUrl = resultData.photoUrl,
                isGoogleLinked = true, // In this REST call, we know it's Google
                idToken = resultData.idToken
            )

            // Update manual user state for platforms with limited SDK (e.g. JVM)
            manualUser.value = user
            saveAuthUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        manualUser.value = null
        saveAuthUser(null)
        firebaseAuth.signOut()
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            logger.i { "deleteAccount started" }
            val sdkUser = firebaseAuth.currentUser
            val manualUserValue = manualUser.value
            val isManual = manualUserValue != null

            if (sdkUser == null && !isManual) {
                throw Exception("No user logged in")
            }

            val uid = sdkUser?.uid ?: manualUserValue?.id
            if (uid != null) {
                try {
                    logger.d { "Attempting to delete Firestore document for user: $uid" }
                    firestore.collection("users").document(uid).delete()
                    logger.d { "Firestore document deleted SUCCESS" }
                } catch (e: Exception) {
                    logger.w(throwable = e) { "Failed to delete Firestore document: ${e.message}" }
                }
            }

            // 1. Try SDK delete if available
            if (sdkUser != null) {
                try {
                    logger.d { "Attempting firebaseAuth.currentUser.delete()" }
                    sdkUser.delete()
                    logger.d { "firebaseAuth.currentUser.delete() SUCCESS" }
                } catch (e: Throwable) {
                    logger.w(throwable = e) { "firebaseAuth.currentUser.delete() FAILED/NOT_IMPLEMENTED" }
                    if (e !is NotImplementedError && e.message?.contains("not implemented") != true) {
                        throw e
                    }
                }
            }

            // 2. Try REST delete if it's a manual user with a token
            if (manualUserValue?.idToken != null) {
                try {
                    logger.d { "Attempting REST deleteAccount" }
                    deleteAccountRest(manualUserValue.idToken).getOrThrow()
                    logger.d { "REST deleteAccount SUCCESS" }
                } catch (e: Exception) {
                    logger.e(throwable = e) { "REST deleteAccount FAILED" }
                    // If SDK delete also failed or wasn't available, we might want to throw here
                    if (sdkUser == null) throw e
                }
            }

            // Clear manual user state for platforms like JVM
            logger.d { "Clearing manualUser and saving null" }
            manualUser.value = null
            saveAuthUser(null)
            
            // Also trigger a sign out to ensure SDK state is cleared and authStateChanged emits null
            try {
                logger.d { "Triggering firebaseAuth.signOut() to clear SDK state" }
                firebaseAuth.signOut()
            } catch (e: Throwable) {
                logger.w { "firebaseAuth.signOut() failed during deleteAccount: ${e.message}" }
            }

            logger.i { "deleteAccount COMPLETED" }
            Result.success(Unit)
        } catch (e: Throwable) {
            logger.e(throwable = e) { "deleteAccount FAILED" }
            Result.failure(e)
        }
    }

    private suspend fun deleteAccountRest(idToken: String): Result<Unit> {
        return try {
            val apiKey = firebaseApiKey ?: throw Exception("Firebase API Key not found")
            val httpClient = HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                    })
                }
            }

            val response = httpClient.post("https://identitytoolkit.googleapis.com/v1/accounts:delete?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(DeleteAccountRequest(idToken = idToken))
            }

            if (response.status.value != 200) {
                val errorBody = response.body<String>()
                throw Exception("Firebase REST Delete failed (${response.status}): $errorBody")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun FirebaseUser.toDomain(): User {
        val googleLinked = try {
            providerData.any { it.providerId == "google.com" }
        } catch (e: Throwable) {
            // Some platforms (like JVM/Desktop) might not implement providerData
            false
        }
        return User(
            id = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoURL,
            isGoogleLinked = googleLinked
        )
    }
}
