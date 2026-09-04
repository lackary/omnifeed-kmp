package io.lackstudio.omnifeed.auth.domain.usecase

import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult

class DeleteAccountUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): UseCaseResult<Unit> {
        return toUseCaseResult(name = "DeleteAccountUseCase") {
            repository.deleteAccount()
        }
    }
}
