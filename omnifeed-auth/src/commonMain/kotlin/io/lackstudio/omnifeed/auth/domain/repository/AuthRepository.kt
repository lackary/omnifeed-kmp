package io.lackstudio.omnifeed.auth.domain.repository

import io.lackstudio.omnifeed.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signUpWithEmail(email: String, password: String, displayName: String? = null): Result<User>
    suspend fun signInWithGoogle(idToken: String, accessToken: String? = null): Result<User>
    suspend fun signInWithCustomService(serviceName: String, accessToken: String): Result<User>
    suspend fun linkWithGoogle(idToken: String, accessToken: String? = null): Result<User>
    suspend fun linkWithCustomService(serviceName: String, accessToken: String): Result<User>
    suspend fun unlinkCustomService(serviceName: String): Result<User>
    suspend fun linkWithEmail(email: String, password: String): Result<User>
    suspend fun updatePassword(newPassword: String): Result<Unit>
    suspend fun unlinkProvider(providerId: String): Result<User>
    suspend fun signOut()
    suspend fun deleteAccount(): Result<Unit>
}
