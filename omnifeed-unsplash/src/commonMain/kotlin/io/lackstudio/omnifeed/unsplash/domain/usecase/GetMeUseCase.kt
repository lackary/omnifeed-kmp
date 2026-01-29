package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.Me
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

class GetMeUseCase(private val repository: UnsplashRepository) : UseCase<Unit, Me> {
    override suspend operator fun invoke(input: Unit): UseCaseResult<Me> {
        return toUseCaseResult { repository.getMe() }
    }
}
