package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.Collection
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

data class GetCollectionsParams(
    val page: Int,
    val perPage: Int
)

class GetCollectionsUseCase(private val repository: UnsplashRepository) : UseCase<GetCollectionsParams, List<Collection>> {
    override suspend operator fun invoke(input: GetCollectionsParams): UseCaseResult<List<Collection>> {
        return toUseCaseResult {
            repository.getCollections(page = input.page, perPage = input.perPage)
        }
    }
}
