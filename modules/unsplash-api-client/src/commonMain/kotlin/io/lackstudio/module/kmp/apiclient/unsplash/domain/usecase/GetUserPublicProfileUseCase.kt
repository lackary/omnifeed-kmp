package io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase

import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCase
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCaseResult
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.toUseCaseResult
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.UserProfile
import io.lackstudio.module.kmp.apiclient.unsplash.domain.repository.UnsplashRepository

class GetUserPublicProfileUseCase(private val repository: UnsplashRepository) : UseCase<String, UserProfile> {
    override suspend operator fun invoke(input: String): UseCaseResult<UserProfile> {
        return toUseCaseResult { repository.getUserPublicProfile(input) }
    }
}
