package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.PhotoDetail
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

class GetPhotoUseCase(private val repository: UnsplashRepository) : UseCase<String, PhotoDetail> {
    override suspend operator fun invoke(input: String): UseCaseResult<PhotoDetail> {
        return toUseCaseResult { repository.getPhoto(input) }
    }
}
