package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.Photo
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

data class GetCollectionPhotosParams(
    val id: String,
    val page: Int,
    val perPage: Int,
    val orientation: String? = null
)

class GetCollectionPhotosUseCase(private val repository: UnsplashRepository) : UseCase<GetCollectionPhotosParams, List<Photo>> {
    override suspend operator fun invoke(input: GetCollectionPhotosParams): UseCaseResult<List<Photo>> {
        return toUseCaseResult {
            repository.getCollectionPhotos(
                id = input.id,
                page = input.page,
                perPage = input.perPage,
                orientation = input.orientation
            )
        }
    }
}
