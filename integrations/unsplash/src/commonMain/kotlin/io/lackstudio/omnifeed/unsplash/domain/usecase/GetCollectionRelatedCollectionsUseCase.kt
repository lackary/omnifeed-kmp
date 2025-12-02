package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.Collection
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

class GetCollectionRelatedCollectionsUseCase(private val repository: UnsplashRepository) : UseCase<String, List<Collection>> {
    override suspend operator fun invoke(input: String): UseCaseResult<List<Collection>> {
        return toUseCaseResult { repository.getCollectionRelatedCollections(input) }
    }
}
