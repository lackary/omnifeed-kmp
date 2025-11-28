package io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase

import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCase
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCaseResult
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.toUseCaseResult
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Topic
import io.lackstudio.module.kmp.apiclient.unsplash.domain.repository.UnsplashRepository

class GetTopicUseCase(private val repository: UnsplashRepository) : UseCase<String, Topic> {
    override suspend operator fun invoke(input: String): UseCaseResult<Topic> {
        return toUseCaseResult { repository.getTopic(input) }
    }
}
