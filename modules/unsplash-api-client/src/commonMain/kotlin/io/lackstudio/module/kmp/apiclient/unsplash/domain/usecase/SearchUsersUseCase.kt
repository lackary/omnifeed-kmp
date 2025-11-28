package io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase

import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCase
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCaseResult
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.toUseCaseResult
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.SearchResults
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.UserProfile
import io.lackstudio.module.kmp.apiclient.unsplash.domain.repository.UnsplashRepository

data class SearchUsersParams(
    val query: String,
    val page: Int,
    val perPage: Int
)

class SearchUsersUseCase(private val repository: UnsplashRepository) : UseCase<SearchUsersParams, SearchResults<UserProfile>> {
    override suspend operator fun invoke(input: SearchUsersParams): UseCaseResult<SearchResults<UserProfile>> {
        return toUseCaseResult {
            repository.searchUsers(
                query = input.query,
                page = input.page,
                perPage = input.perPage
            )
        }
    }
}
