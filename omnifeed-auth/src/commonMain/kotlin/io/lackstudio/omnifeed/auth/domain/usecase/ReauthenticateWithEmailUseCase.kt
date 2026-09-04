package io.lackstudio.omnifeed.auth.domain.usecase

import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult

class ReauthenticateWithEmailUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(password: String): UseCaseResult<Unit> {
        return toUseCaseResult(name = "ReauthenticateWithEmailUseCase") {
            repository.reauthenticateWithEmail(password)
        }
    }
}
