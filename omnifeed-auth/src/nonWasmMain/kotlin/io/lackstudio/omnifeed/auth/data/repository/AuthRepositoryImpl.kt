package io.lackstudio.omnifeed.auth.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.data.model.request.SignInWithIdpRequest
import io.lackstudio.omnifeed.auth.data.model.response.SignInWithIdpResponse
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.auth.platform.firebaseApiKey
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json


class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    private val manualUser = MutableStateFlow<User?>(null)

    override val currentUser: Flow<User?> = combine(
        firebaseAuth.authStateChanged.map { it?.toDomain() },
        manualUser
    ) { sdkUser, manualUser -> 
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
                photoUrl = resultData.photoUrl
            )

            // Update manual user state for platforms with limited SDK (e.g. JVM)
            manualUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        manualUser.value = null
        firebaseAuth.signOut()
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.delete() ?: throw Exception("No user logged in")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun FirebaseUser.toDomain(): User {
        return User(
            id = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoURL
        )
    }
}
