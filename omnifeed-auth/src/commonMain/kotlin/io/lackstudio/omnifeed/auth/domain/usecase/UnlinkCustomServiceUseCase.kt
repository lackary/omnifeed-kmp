package io.lackstudio.omnifeed.auth.domain.usecase

import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult

class UnlinkCustomServiceUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(serviceName: String): UseCaseResult<User> {
        return toUseCaseResult(name = "UnlinkCustomServiceUseCase") {
            repository.unlinkCustomService(serviceName)
        }
    }
}
