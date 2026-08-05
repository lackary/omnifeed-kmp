package io.lackstudio.omnifeed.auth.data.local.source

import io.lackstudio.omnifeed.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthLocalDataSource {
    // Firebase related
    val user: Flow<User?>
    fun saveUser(user: User?)
    fun getUser(): User?
    
    // External services related - Bound to userId for security
    suspend fun saveServiceToken(userId: String, serviceName: String, token: String)
    suspend fun getServiceToken(userId: String, serviceName: String): String?
    suspend fun clearServiceToken(userId: String, serviceName: String)
    suspend fun clearAllServiceTokens()
}
