package io.lackstudio.omnifeed.auth.domain.repository

import io.lackstudio.omnifeed.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInWithEmail(email: String, password: String): User
    suspend fun signUpWithEmail(email: String, password: String, username: String? = null): User
    suspend fun signInWithGoogle(idToken: String, accessToken: String? = null): User
    suspend fun signInWithCustomService(serviceName: String, accessToken: String): User
    suspend fun linkWithGoogle(idToken: String, accessToken: String? = null): User
    suspend fun linkWithCustomService(serviceName: String, accessToken: String): User
    suspend fun unlinkCustomService(serviceName: String): User
    suspend fun linkWithEmail(email: String, password: String): User
    suspend fun updatePassword(newPassword: String)
    suspend fun unlinkProvider(providerId: String): User
    suspend fun signOut()
    suspend fun deleteAccount()

    // Get stored OAuth2 token for a specific service
    suspend fun getServiceToken(serviceName: String): String?
}
