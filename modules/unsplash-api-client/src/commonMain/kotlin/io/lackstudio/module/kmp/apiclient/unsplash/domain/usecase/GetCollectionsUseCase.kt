package io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase

import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCase
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCaseResult
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.toUseCaseResult
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Collection
import io.lackstudio.module.kmp.apiclient.unsplash.domain.repository.UnsplashRepository

data class GetCollectionsParams(
    val page: Int,
    val perPage: Int
)

class GetCollectionsUseCase(private val repository: UnsplashRepository) : UseCase<GetCollectionsParams, List<Collection>> {
    override suspend operator fun invoke(input: GetCollectionsParams): UseCaseResult<List<Collection>> {
        return toUseCaseResult {
            repository.getCollections(page = input.page, perPage = input.perPage)
        }
    }
}
