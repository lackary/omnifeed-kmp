package io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase

import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCase
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCaseResult
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.toUseCaseResult
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Collection
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.SearchResults
import io.lackstudio.module.kmp.apiclient.unsplash.domain.repository.UnsplashRepository

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
