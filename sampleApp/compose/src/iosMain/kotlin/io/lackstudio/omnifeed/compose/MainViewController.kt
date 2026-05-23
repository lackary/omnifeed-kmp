package io.lackstudio.omnifeed.compose

import androidx.compose.ui.window.ComposeUIViewController
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import io.lackstudio.omnifeed.compose.di.initKoin
import io.lackstudio.omnifeed.compose.ui.screen.App

import platform.UIKit.UIViewController

@Suppress("unused", "FunctionName")
fun MainViewController(): UIViewController {
    val globalLogger = Logger.withTag("OmniHub")
    val appHttpClient = HttpClient(Darwin) {
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
    initKoin(globalLogger, appHttpClient)

//    initializeKoin(
//        allModules = listOf(
//            coreModule(),
//            unsplashModule(
//                tokenType = AUTH_SCHEME_PUBLIC,
//                token = getUnsplashAccessKey()
//            ),
//            viewModelModule
//        )
//    )
    return ComposeUIViewController { App() }
}
