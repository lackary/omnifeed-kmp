package io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase

import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCase
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCaseResult
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.toUseCaseResult
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Photo
import io.lackstudio.module.kmp.apiclient.unsplash.domain.repository.UnsplashRepository

data class GetTopicPhotosParams(
    val idOrSlug: String,
    val page: Int,
    val perPage: Int,
    val orientation: String? = null,
    val orderBy: String? = null
)

class GetTopicPhotosUseCase(private val repository: UnsplashRepository) : UseCase<GetTopicPhotosParams, List<Photo>> {
    override suspend operator fun invoke(input: GetTopicPhotosParams): UseCaseResult<List<Photo>> {
        return toUseCaseResult {
            repository.getTopicPhotos(
                idOrSlug = input.idOrSlug,
                page = input.page,
                perPage = input.perPage,
                orientation = input.orientation,
                orderBy = input.orderBy
            )
        }
    }
}
