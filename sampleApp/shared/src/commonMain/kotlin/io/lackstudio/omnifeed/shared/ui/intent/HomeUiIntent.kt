package io.lackstudio.omnifeed.shared.ui.intent

sealed class HomeUiIntent {
    data object LoadPhotos : HomeUiIntent()
    data object GetProfile : HomeUiIntent()
    data class ExchangeOAuth(val code: String) : HomeUiIntent()
}
