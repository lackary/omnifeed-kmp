package io.lackstudio.omnifeed.auth.domain.usecase

import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository

class LinkWithUnsplashUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(accessToken: String): Result<User> {
        return repository.linkWithUnsplash(accessToken)
    }
}
