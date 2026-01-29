package io.lackstudio.omnifeed.unsplash.domain.usecase

import io.lackstudio.omnifeed.core.domain.usecase.UseCase
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.core.domain.usecase.toUseCaseResult
import io.lackstudio.omnifeed.unsplash.domain.model.OAuthCode
import io.lackstudio.omnifeed.unsplash.domain.model.OAuthToken
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository

class ExchangeOAuthUseCase(private val repository: UnsplashRepository) :
    UseCase<OAuthCode, OAuthToken> {
        override suspend operator fun invoke(input: OAuthCode): UseCaseResult<OAuthToken> {
        return toUseCaseResult {
            repository.exchangeOAuth(input)
        }
    }
}
