package io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase

import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCase
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCaseResult
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.toUseCaseResult
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Photo
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.SearchResults
import io.lackstudio.module.kmp.apiclient.unsplash.domain.repository.UnsplashRepository

data class SearchPhotosParams(
    val query: String,
    val page: Int,
    val perPage: Int,
    val orderBy: String? = null,
    val collections: String? = null,
    val contentFilter: String? = null,
    val color: String? = null,
    val orientation: String? = null
)

class SearchPhotosUseCase(private val repository: UnsplashRepository) : UseCase<SearchPhotosParams, SearchResults<Photo>> {
    override suspend operator fun invoke(input: SearchPhotosParams): UseCaseResult<SearchResults<Photo>> {
        return toUseCaseResult {
            repository.searchPhotos(
                query = input.query,
                page = input.page,
                perPage = input.perPage,
                orderBy = input.orderBy,
                collections = input.collections,
                contentFilter = input.contentFilter,
                color = input.color,
                orientation = input.orientation
            )
        }
    }
}
