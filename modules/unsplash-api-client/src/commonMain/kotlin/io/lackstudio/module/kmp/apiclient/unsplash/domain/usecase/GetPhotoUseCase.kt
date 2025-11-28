package io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase

import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCase
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCaseResult
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.toUseCaseResult
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.PhotoDetail
import io.lackstudio.module.kmp.apiclient.unsplash.domain.repository.UnsplashRepository

class GetPhotoUseCase(private val repository: UnsplashRepository) : UseCase<String, PhotoDetail> {
    override suspend operator fun invoke(input: String): UseCaseResult<PhotoDetail> {
        return toUseCaseResult { repository.getPhoto(input) }
    }
}
