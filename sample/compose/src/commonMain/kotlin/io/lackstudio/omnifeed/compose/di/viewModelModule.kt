package io.lackstudio.omnifeed.compose.di

import co.touchlab.kermit.Logger
import io.lackstudio.omnifeed.OmniFeed
import io.lackstudio.omnifeed.compose.ui.viewmodel.AppViewModel
import org.koin.dsl.module

val viewModelModule = module {

    factory { OmniFeed.getPhotosUseCase }
    factory { OmniFeed.exchangeOAuthUseCase }
    factory { OmniFeed.getMeUseCase }

    single { OmniFeed.accessTokenProvider }

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
