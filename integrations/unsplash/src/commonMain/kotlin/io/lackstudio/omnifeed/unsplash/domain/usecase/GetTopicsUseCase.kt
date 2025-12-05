package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.Topic
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

data class GetTopicsParams(
    val page: Int,
    val perPage: Int
)

class GetTopicsUseCase(private val repository: UnsplashRepository) : UseCase<GetTopicsParams, List<Topic>> {
    override suspend operator fun invoke(input: GetTopicsParams): UseCaseResult<List<Topic>> {
        return toUseCaseResult {
            repository.getTopics(page = input.page, perPage = input.perPage)
        }
    }
}
