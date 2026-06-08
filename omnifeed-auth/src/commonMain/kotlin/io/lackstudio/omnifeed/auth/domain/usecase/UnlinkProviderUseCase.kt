package io.lackstudio.omnifeed.auth.domain.usecase

import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository

class UnlinkProviderUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(providerId: String): Result<User> {
        return repository.unlinkProvider(providerId)
    }
}
