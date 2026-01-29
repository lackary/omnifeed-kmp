package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.Photo
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

data class GetTopicPhotosParams(
    val idOrSlug: String,
    val page: Int,
    val perPage: Int,
    val orientation: String? = null,
    val orderBy: String? = null
)

class GetTopicPhotosUseCase(private val repository: UnsplashRepository) : UseCase<GetTopicPhotosParams, List<Photo>> {
    override suspend operator fun invoke(input: GetTopicPhotosParams): UseCaseResult<List<Photo>> {
        return toUseCaseResult {
            repository.getTopicPhotos(
                idOrSlug = input.idOrSlug,
                page = input.page,
                perPage = input.perPage,
                orientation = input.orientation,
                orderBy = input.orderBy
            )
        }
    }
}
