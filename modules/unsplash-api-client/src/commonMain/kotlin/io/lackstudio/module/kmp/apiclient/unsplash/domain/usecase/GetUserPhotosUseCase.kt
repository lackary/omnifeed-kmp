package io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase

import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCase
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCaseResult
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.toUseCaseResult
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Photo
import io.lackstudio.module.kmp.apiclient.unsplash.domain.repository.UnsplashRepository

data class GetUserPhotosParams(
    val username: String,
    val page: Int,
    val perPage: Int,
    val orderBy: String? = null,
    val stats: Boolean? = null,
    val quantity: Int? = null,
    val orientation: String? = null
)

class GetUserPhotosUseCase(private val repository: UnsplashRepository) : UseCase<GetUserPhotosParams, List<Photo>> {
    override suspend operator fun invoke(input: GetUserPhotosParams): UseCaseResult<List<Photo>> {
        return toUseCaseResult {
            repository.getUserPhotos(
                username = input.username,
                page = input.page,
                perPage = input.perPage,
                orderBy = input.orderBy,
                stats = input.stats,
                quantity = input.quantity,
                orientation = input.orientation
            )
        }
    }
}
