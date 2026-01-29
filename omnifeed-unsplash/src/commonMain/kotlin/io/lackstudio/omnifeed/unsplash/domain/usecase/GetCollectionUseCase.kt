package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.Collection
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

class GetCollectionUseCase(private val repository: UnsplashRepository) : UseCase<String, Collection> {
    override suspend operator fun invoke(input: String): UseCaseResult<Collection> {
        return toUseCaseResult { repository.getCollection(input) }
    }
}
