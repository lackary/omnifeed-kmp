package io.lackstudio.omnifeed.auth.domain.usecase

import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult

class UpdatePasswordUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(newPassword: String, oldPassword: String? = null): UseCaseResult<Unit> {
        return toUseCaseResult(name = "UpdatePasswordUseCase") {
            repository.updatePassword(newPassword, oldPassword)
        }
    }
}
