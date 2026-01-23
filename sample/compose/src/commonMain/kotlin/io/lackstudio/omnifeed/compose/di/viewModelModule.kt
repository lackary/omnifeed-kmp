package io.lackstudio.omnifeed.compose.di

import io.lackstudio.omnifeed.compose.ui.viewmodel.AppViewModel
import org.koin.dsl.module

val viewModelModule = module {

    factory {
        AppViewModel(
            getPhotosUseCase = get(),
            exchangeOAuthUseCase = get(),
            getMeUseCase = get(),
            appLogger = get(),
            accessTokenProvider = get()
        )
    }
}
