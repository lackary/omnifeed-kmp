package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.Collection
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

data class GetUserCollectionsParams(
    val username: String,
    val page: Int,
    val perPage: Int
)

class GetUserCollectionsUseCase(private val repository: UnsplashRepository) : UseCase<GetUserCollectionsParams, List<Collection>> {
    override suspend operator fun invoke(input: GetUserCollectionsParams): UseCaseResult<List<Collection>> {
        return toUseCaseResult {
            repository.getUserCollections(
                username = input.username,
                page = input.page,
                perPage = input.perPage
            )
        }
    }
}
