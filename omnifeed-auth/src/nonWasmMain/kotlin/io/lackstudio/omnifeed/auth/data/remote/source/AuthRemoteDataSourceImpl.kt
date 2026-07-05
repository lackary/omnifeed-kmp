package io.lackstudio.omnifeed.auth.data.remote.source

import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.lackstudio.omnifeed.auth.data.remote.api.FirebaseApiService
import io.lackstudio.omnifeed.auth.data.remote.model.request.*
import io.lackstudio.omnifeed.auth.data.remote.model.dto.UserProfileDto
import io.lackstudio.omnifeed.auth.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRemoteDataSourceImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val apiService: FirebaseApiService
) : AuthRemoteDataSource {

    override val authStateChanged: Flow<FirebaseUser?> = firebaseAuth.authStateChanged
    override val currentUser: FirebaseUser? get() = firebaseAuth.currentUser

    override suspend fun signInWithEmail(email: String, password: String): FirebaseUser {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password)
        return result.user ?: throw Exception("Login failed: User is null")
    }

    override suspend fun signUpWithEmail(email: String, password: String): FirebaseUser {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
        return result.user ?: throw Exception("Registration failed: User is null")
    }

    override suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser {
        val result = firebaseAuth.signInWithCredential(credential)
        return result.user ?: throw Exception("Login with credential failed: User is null")
    }

    override suspend fun signInWithCustomToken(token: String): FirebaseUser {
        val result = firebaseAuth.signInWithCustomToken(token)
        return result.user ?: throw Exception("Login with custom token failed: User is null")
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun fetchFirebaseCustomToken(endpoint: String, customAccessToken: String, provider: String): String {
        return apiService.fetchFirebaseCustomToken(endpoint, customAccessToken, provider)
    }

    override suspend fun signInWithGoogleRest(idToken: String): User {
        val resultData = apiService.signInWithIdp(SignInWithIdpRequest(
            postBody = "id_token=$idToken&providerId=google.com"
        ))

        return User(
            id = resultData.localId ?: throw Exception("Login failed: localId is null"),
            email = resultData.email,
            displayName = resultData.displayName,
            photoUrl = resultData.photoUrl,
            isGoogleLinked = true,
            idToken = resultData.idToken
        )
    }

    override suspend fun signInWithCustomTokenRest(customToken: String, serviceName: String, accessToken: String): User {
        val resultData = apiService.signInWithCustomToken(SignInWithCustomTokenRequest(token = customToken))
        val firebaseIdToken = resultData.idToken ?: throw Exception("REST login failed: idToken is null")

        val lookupData = apiService.lookup(LookupRequest(idToken = firebaseIdToken))
        val userInfo = lookupData.users.firstOrNull() ?: throw Exception("REST login failed: No user info found")

        return User(
            id = userInfo.localId,
            email = userInfo.email,
            displayName = userInfo.displayName,
            photoUrl = userInfo.photoUrl,
            isGoogleLinked = userInfo.providerUserInfo.any { it.providerId == "google.com" },
            customLinkedServices = mapOf(serviceName to true),
            idToken = firebaseIdToken
        )
    }

    override suspend fun linkWithGoogleRest(idToken: String, currentFirebaseIdToken: String): User {
        val resultData = apiService.signInWithIdp(SignInWithIdpRequest(
            postBody = "id_token=$idToken&providerId=google.com",
            idToken = currentFirebaseIdToken,
            requestUri = "http://localhost"
        ))

        return User(
            id = resultData.localId ?: throw Exception("Linking failed: localId is null"),
            email = resultData.email,
            displayName = resultData.displayName,
            photoUrl = resultData.photoUrl,
            isGoogleLinked = true,
            idToken = resultData.idToken
        )
    }

    override suspend fun deleteAccountRest(idToken: String) {
        apiService.deleteAccount(DeleteAccountRequest(idToken = idToken))
    }

    override fun getUserProfile(uid: String, serviceFields: List<String>): Flow<UserProfileDto?> {
        return firestore.collection("users").document(uid).snapshots().map { snapshot ->
            if (snapshot.exists) {
                // Retrieve fields with known types individually; this is safe in GitLive
                val customFieldsMap = serviceFields.associateWith { field ->
                    try {
                        snapshot.get<Boolean?>(field) ?: false
                    } catch (e: Exception) {
                        false
                    }
                }

                UserProfileDto(
                    displayName = try { snapshot.get<String?>("displayName") } catch (e: Exception) { null },
                    email = try { snapshot.get<String?>("email") } catch (e: Exception) { null },
                    photoUrl = try { snapshot.get<String?>("photoUrl") } catch (e: Exception) { null },
                    isGoogleLinked = try { snapshot.get<Boolean?>("isGoogleLinked") ?: false } catch (e: Exception) { false },
                    customFields = customFieldsMap
                )
            } else {
                null
            }
        }
    }

    override suspend fun saveUserProfile(uid: String, data: UserProfileDto) {
        firestore.collection("users").document(uid).set(data, merge = true)
    }

    override suspend fun updateCustomField(uid: String, field: String, value: Boolean) {
        firestore.collection("users").document(uid).update(field to value)
    }

    override suspend fun deleteUserProfile(uid: String) {
        firestore.collection("users").document(uid).delete()
    }
}
