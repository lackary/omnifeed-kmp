package io.lackstudio.omnifeed.app.di

import io.lackstudio.omnifeed.app.ui.viewmodel.AppViewModel
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
