package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.Photo
import io.lackstudio.omnifeed.unsplash.domain.model.SearchResults
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

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
