package io.lackstudio.omnifeed.auth.data.remote.source

import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseUser
import io.lackstudio.omnifeed.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

import io.lackstudio.omnifeed.auth.data.remote.model.dto.UserProfileDto

interface AuthRemoteDataSource {
    val authStateChanged: Flow<FirebaseUser?>
    val currentUser: FirebaseUser?

    suspend fun signInWithEmail(email: String, password: String): FirebaseUser
    suspend fun signUpWithEmail(email: String, password: String): FirebaseUser
    suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser
    suspend fun signInWithCustomToken(token: String): FirebaseUser
    suspend fun signOut()
    
    // REST fallbacks / Helpers
    suspend fun fetchFirebaseCustomToken(endpoint: String, customAccessToken: String, provider: String): String
    suspend fun signInWithGoogleRest(idToken: String): User
    suspend fun signInWithCustomTokenRest(customToken: String, serviceName: String, accessToken: String): User
    suspend fun linkWithGoogleRest(idToken: String, currentFirebaseIdToken: String): User
    suspend fun deleteAccountRest(idToken: String)

    // Firestore / User Profile
    fun getUserProfile(uid: String, serviceFields: List<String>): Flow<UserProfileDto?>
    suspend fun getUserProfileRest(uid: String, idToken: String): UserProfileDto?
    suspend fun saveUserProfile(uid: String, profile: UserProfileDto)
    suspend fun saveUserProfileRest(uid: String, idToken: String, profile: UserProfileDto)
    suspend fun updateCustomField(uid: String, field: String, value: Boolean)
    suspend fun updateCustomFieldRest(uid: String, idToken: String, field: String, value: Boolean)
    suspend fun deleteUserProfile(uid: String)
    suspend fun deleteUserProfileRest(uid: String, idToken: String)
}
