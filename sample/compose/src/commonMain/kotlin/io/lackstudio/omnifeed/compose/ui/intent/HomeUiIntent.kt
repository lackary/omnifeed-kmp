package io.lackstudio.omnifeed.compose.ui.intent

sealed class HomeUiIntent {
    data object LoadPhotos : HomeUiIntent()
    data object GetProfile : HomeUiIntent()
    data class ExchangeOAuth(val code: String) : HomeUiIntent()
}
