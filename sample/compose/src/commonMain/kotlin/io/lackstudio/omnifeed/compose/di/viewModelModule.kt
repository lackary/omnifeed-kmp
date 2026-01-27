package io.lackstudio.omnifeed.compose.di

import co.touchlab.kermit.Logger
import io.lackstudio.omnifeed.compose.ui.viewmodel.AppViewModel
import org.koin.dsl.module

val viewModelModule = module {

    factory {
        AppViewModel(
            getPhotosUseCase = get(),
            exchangeOAuthUseCase = get(),
            getMeUseCase = get(),
            accessTokenProvider = get(),
            logger = get<Logger>().withTag("AppViewModel")
        )
    }
}
