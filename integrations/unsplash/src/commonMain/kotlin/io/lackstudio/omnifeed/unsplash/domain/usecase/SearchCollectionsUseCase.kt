package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.Collection
import io.lackstudio.omnifeed.unsplash.domain.model.SearchResults
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

data class SearchCollectionsParams(
    val query: String,
    val page: Int,
    val perPage: Int
)

class SearchCollectionsUseCase(private val repository: UnsplashRepository) : UseCase<SearchCollectionsParams, SearchResults<Collection>> {
    override suspend operator fun invoke(input: SearchCollectionsParams): UseCaseResult<SearchResults<Collection>> {
        return toUseCaseResult {
            repository.searchCollections(
                query = input.query,
                page = input.page,
                perPage = input.perPage
            )
        }
    }
}
