package io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase

import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCase
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCaseResult
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.toUseCaseResult
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Collection
import io.lackstudio.module.kmp.apiclient.unsplash.domain.repository.UnsplashRepository

class GetCollectionRelatedCollectionsUseCase(private val repository: UnsplashRepository) : UseCase<String, List<Collection>> {
    override suspend operator fun invoke(input: String): UseCaseResult<List<Collection>> {
        return toUseCaseResult { repository.getCollectionRelatedCollections(input) }
    }
}
