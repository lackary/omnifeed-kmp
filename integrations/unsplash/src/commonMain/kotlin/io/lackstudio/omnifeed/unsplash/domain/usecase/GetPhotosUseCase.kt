package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.Photo
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

data class GetPhotosParams(val page: Int, val perPage: Int)

class GetPhotosUseCase(private val repository: UnsplashRepository) : UseCase<GetPhotosParams, List<Photo>> {
    override suspend operator fun invoke(input: GetPhotosParams): UseCaseResult<List<Photo>> {
        return toUseCaseResult { repository.getPhotos(input.page, input.perPage) }
    }
}
