package io.lackstudio.omnifeed.auth.domain.usecase

import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult

class ReauthenticateWithGoogleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String, accessToken: String? = null): UseCaseResult<Unit> {
        return toUseCaseResult(name = "ReauthenticateWithGoogleUseCase") {
            repository.reauthenticateWithGoogle(idToken, accessToken)
        }
    }
}
