package io.lackstudio.omnifeed.auth.data.local.source

import io.lackstudio.omnifeed.auth.data.local.model.UserServiceTokens
import io.lackstudio.omnifeed.auth.data.storage.LocalStorage
import io.lackstudio.omnifeed.auth.data.storage.getFireBaseAuth
import io.lackstudio.omnifeed.auth.data.storage.getOrNull
import io.lackstudio.omnifeed.auth.data.storage.save
import io.lackstudio.omnifeed.auth.data.storage.saveFirebaseAuth
import io.lackstudio.omnifeed.auth.domain.model.User

/**
 * Local Data Structures:
 *
 * 1. userCacheStorage (LocalStorage):
 *    ├── Key: "firebase_auth_user_key"
 *    └── Value: User object (cached profile for offline access)
 *
 * 2. serviceTokenStorage (LocalStorage):
 *    ├── Key: {userId} (String)
 *    └── Value: UserServiceTokens object
 *          ├── userId: String
 *          └── tokens: Map<String, String> (e.g., {"unsplash": "access_token_abc"})
 */
class AuthLocalDataSourceImpl(
    private val userCacheStorage: LocalStorage,
    private val serviceTokenStorage: LocalStorage
) : AuthLocalDataSource {

    override fun saveUser(user: User?) {
        userCacheStorage.saveFirebaseAuth(user)
    }

    override fun getUser(): User? {
        return userCacheStorage.getFireBaseAuth()
    }

    override suspend fun saveServiceToken(userId: String, serviceName: String, token: String) {
        if (userId.isBlank()) return
        
        val userServiceTokens = serviceTokenStorage.getOrNull<UserServiceTokens>(userId) 
            ?: UserServiceTokens(userId = userId)
            
        val updatedTokens = userServiceTokens.tokens.toMutableMap().apply {
            put(serviceName, token)
        }
        
        serviceTokenStorage.save(userId, userServiceTokens.copy(tokens = updatedTokens))
    }

    override suspend fun getServiceToken(userId: String, serviceName: String): String? {
        if (userId.isBlank()) return null
        
        val userServiceTokens = serviceTokenStorage.getOrNull<UserServiceTokens>(userId)
        return userServiceTokens?.tokens?.get(serviceName)
    }

    override suspend fun clearServiceToken(userId: String, serviceName: String) {
        if (userId.isBlank()) return
        
        val userServiceTokens = serviceTokenStorage.getOrNull<UserServiceTokens>(userId)
        if (userServiceTokens != null) {
            val updatedTokens = userServiceTokens.tokens.toMutableMap().apply {
                remove(serviceName)
            }
            serviceTokenStorage.save(userId, userServiceTokens.copy(tokens = updatedTokens))
        }
    }

    override suspend fun clearAllServiceTokens() {
        serviceTokenStorage.clearAll()
    }
}
