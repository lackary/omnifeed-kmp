package io.lackstudio.omnifeed.auth.domain.usecase

import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult

class LinkWithGoogleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String, accessToken: String? = null): UseCaseResult<User> {
        return toUseCaseResult(name = "LinkWithGoogleUseCase") {
            repository.linkWithGoogle(idToken, accessToken)
        }
    }
}
