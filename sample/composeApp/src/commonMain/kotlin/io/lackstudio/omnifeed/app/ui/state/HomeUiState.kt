package io.lackstudio.omnifeed.app.ui.state

import io.lackstudio.omnifeed.ui.state.AppUiState
import io.lackstudio.omnifeed.unsplash.domain.model.Me as UnsplashMe
import io.lackstudio.omnifeed.unsplash.domain.model.OAuthToken as UnsplashOAuthToken
import io.lackstudio.omnifeed.unsplash.domain.model.Photo as UnsplashPhoto

data class HomeUiState(
    val photos: AppUiState<List<UnsplashPhoto>> = AppUiState.Idle,
    val profile: AppUiState<UnsplashMe> = AppUiState.Idle,
    val oAuthToken: AppUiState<UnsplashOAuthToken> = AppUiState.Idle,
)
