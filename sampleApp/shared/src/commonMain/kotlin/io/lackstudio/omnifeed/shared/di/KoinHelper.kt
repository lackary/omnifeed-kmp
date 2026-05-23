package io.lackstudio.omnifeed.shared.di

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.lackstudio.omnifeed.OmniFeed
import io.lackstudio.omnifeed.shared.platform.getUnsplashAccessKey
import io.lackstudio.omnifeed.core.OmniFeedConfig
import io.lackstudio.omnifeed.core.UnsplashConfig
import io.lackstudio.omnifeed.unsplash.utils.Environment.AUTH_SCHEME_PUBLIC
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(logger: Logger, appHttpClient: HttpClient, config: KoinAppDeclaration? = null) {
    OmniFeed.initialize(OmniFeedConfig(
        appLogger = logger,
        unsplash = UnsplashConfig(
            tokenType = AUTH_SCHEME_PUBLIC,
            token = getUnsplashAccessKey()
        )
    ))
    startKoin {
        config?.invoke(this) // Allow platforms to pass extra configuration (e.g., Android Context)
        modules(
            module{
                single<Logger> { logger }
                single<HttpClient> { appHttpClient }
            },
            viewModelModule
        )
    }
}
