package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.Photo
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

data class GetUserLikedPhotosParams(
    val username: String,
    val page: Int,
    val perPage: Int,
    val orderBy: String? = null,
    val orientation: String? = null
)

class GetUserLikedPhotosUseCase(private val repository: UnsplashRepository) : UseCase<GetUserLikedPhotosParams, List<Photo>> {
    override suspend operator fun invoke(input: GetUserLikedPhotosParams): UseCaseResult<List<Photo>> {
        return toUseCaseResult {
            repository.getUserLikedPhotos(
                username = input.username,
                page = input.page,
                perPage = input.perPage,
                orderBy = input.orderBy,
                orientation = input.orientation
            )
        }
    }
}
