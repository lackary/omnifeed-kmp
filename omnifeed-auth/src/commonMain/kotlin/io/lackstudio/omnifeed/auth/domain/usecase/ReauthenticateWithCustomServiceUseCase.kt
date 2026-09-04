package io.lackstudio.omnifeed.auth.domain.usecase

import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult

class ReauthenticateWithCustomServiceUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(serviceName: String, accessToken: String): UseCaseResult<Unit> {
        return toUseCaseResult(name = "ReauthenticateWithCustomServiceUseCase") {
            repository.reauthenticateWithCustomService(serviceName, accessToken)
        }
    }
}
