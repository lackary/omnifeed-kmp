package io.lackstudio.omnifeed.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import io.lackstudio.omnifeed.shared.di.initKoin
import org.koin.dsl.module

class ExampleApplication : Application() {
    // init the modules of DI
    override fun onCreate() {
        super.onCreate()
        val globalLogger = Logger.withTag("OmniHub")
        val appHttpClient = HttpClient(Android) {
            install(Resources) // This is essential for hrefWithHost

            // Recommended to install these to prevent unexpected errors later
            install(Logging) {
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        globalLogger.d(tag = "AppHttpClient") { message }
                    }
                }
            }
            install(ContentNegotiation) {
                json()
            }
        }
        initKoin(globalLogger, appHttpClient) {
            androidContext(this@ExampleApplication)
        }
//        initializeKoin(
//            allModules = listOf(
//                coreModule(),
//                unsplashModule(
//                    tokenType = Environment.AUTH_SCHEME_PUBLIC,
//                    token = getUnsplashAccessKey()
//                ),
//                viewModelModule,
//            )
//        )

//        AppInitializer.onApplicationStart(applicationContext.getString(R.string.default_web_client_id))

    }
}
