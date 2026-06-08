package io.lackstudio.omnifeed.auth.data.repository

import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.data.model.request.SignInWithIdpRequest
import io.lackstudio.omnifeed.auth.data.model.request.DeleteAccountRequest
import io.lackstudio.omnifeed.auth.data.model.response.SignInWithIdpResponse
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.auth.platform.firebaseApiKey
import io.lackstudio.omnifeed.auth.platform.loadAuthUser
import io.lackstudio.omnifeed.auth.platform.saveAuthUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json


class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    private val logger = Logger.withTag("AuthRepositoryImpl")
    private val manualUser = MutableStateFlow<User?>(null)

    init {
        // Load user from persistent storage (used for platforms like JVM with REST fallback)
        manualUser.value = loadAuthUser()
    }

    override val currentUser: Flow<User?> = combine(
        firebaseAuth.authStateChanged.map { it?.toDomain() },
        manualUser
    ) { sdkUser, manualUser -> 
        logger.d { "currentUser: sdkUser=$sdkUser, manualUser=$manualUser" }
        manualUser ?: sdkUser 
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

    override suspend fun linkWithGoogle(idToken: String, accessToken: String?): Result<User> {
        return try {
            val user = firebaseAuth.currentUser ?: throw Exception("No user logged in to link with")
            val credential = GoogleAuthProvider.credential(idToken, accessToken)
            val result = user.linkWithCredential(credential)
            val linkedUser = result.user?.toDomain() ?: throw Exception("Google linking failed: User is null")
            
            // Force set as linked and update manual state if exists
            val finalUser = linkedUser.copy(isGoogleLinked = true)
            if (manualUser.value != null) {
                manualUser.value = finalUser
                saveAuthUser(finalUser)
            }
            
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
                        encodeDefaults = true // 確保 requestUri 等預設值會被放入 JSON
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
