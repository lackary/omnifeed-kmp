package io.lackstudio.omnifeed.auth.data.local.source

import io.lackstudio.omnifeed.auth.data.local.model.UserServiceTokens
import io.lackstudio.omnifeed.auth.data.storage.LocalStorage
import io.lackstudio.omnifeed.auth.data.storage.getFireBaseAuth
import io.lackstudio.omnifeed.auth.data.storage.getOrNull
import io.lackstudio.omnifeed.auth.data.storage.save
import io.lackstudio.omnifeed.auth.data.storage.saveFirebaseAuth
import io.lackstudio.omnifeed.auth.domain.model.User

class AuthLocalDataSourceImpl(
    private val firebaseStorage: LocalStorage,
    private val serviceStorage: LocalStorage
) : AuthLocalDataSource {

    override fun saveUser(user: User?) {
        firebaseStorage.saveFirebaseAuth(user)
    }

    override fun getUser(): User? {
        return firebaseStorage.getFireBaseAuth()
    }

    override suspend fun saveServiceToken(userId: String, serviceName: String, token: String) {
        if (userId.isBlank()) return
        
        val userServiceTokens = serviceStorage.getOrNull<UserServiceTokens>(userId) 
            ?: UserServiceTokens(userId = userId)
            
        val updatedTokens = userServiceTokens.tokens.toMutableMap().apply {
            put(serviceName, token)
        }
        
        serviceStorage.save(userId, userServiceTokens.copy(tokens = updatedTokens))
    }

    override suspend fun getServiceToken(userId: String, serviceName: String): String? {
        if (userId.isBlank()) return null
        
        val userServiceTokens = serviceStorage.getOrNull<UserServiceTokens>(userId)
        return userServiceTokens?.tokens?.get(serviceName)
    }

    override suspend fun clearServiceToken(userId: String, serviceName: String) {
        if (userId.isBlank()) return
        
        val userServiceTokens = serviceStorage.getOrNull<UserServiceTokens>(userId)
        if (userServiceTokens != null) {
            val updatedTokens = userServiceTokens.tokens.toMutableMap().apply {
                remove(serviceName)
            }
            serviceStorage.save(userId, userServiceTokens.copy(tokens = updatedTokens))
        }
    }

    override suspend fun clearAllServiceTokens() {
        serviceStorage.clearAll()
    }
}
