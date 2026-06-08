package io.lackstudio.omnifeed.auth.domain.usecase

import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository

class LinkWithGoogleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String, accessToken: String? = null): Result<User> {
        return repository.linkWithGoogle(idToken, accessToken)
    }
}
