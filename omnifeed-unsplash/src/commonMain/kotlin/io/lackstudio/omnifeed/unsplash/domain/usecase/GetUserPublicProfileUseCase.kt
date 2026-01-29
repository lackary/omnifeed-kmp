package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.UserProfile
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

class GetUserPublicProfileUseCase(private val repository: UnsplashRepository) : UseCase<String, UserProfile> {
    override suspend operator fun invoke(input: String): UseCaseResult<UserProfile> {
        return toUseCaseResult { repository.getUserPublicProfile(input) }
    }
}
