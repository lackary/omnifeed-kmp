package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.Topic
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

class GetTopicUseCase(private val repository: UnsplashRepository) : UseCase<String, Topic> {
    override suspend operator fun invoke(input: String): UseCaseResult<Topic> {
        return toUseCaseResult { repository.getTopic(input) }
    }
}
