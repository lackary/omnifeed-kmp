package io.lackstudio.omnifeed.auth.domain.usecase

import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository

class UnlinkCustomServiceUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(serviceName: String): Result<User> {
        return repository.unlinkCustomService(serviceName)
    }
}
