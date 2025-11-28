package io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase

import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCase
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCaseResult
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.toUseCaseResult
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Topic
import io.lackstudio.module.kmp.apiclient.unsplash.domain.repository.UnsplashRepository

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
