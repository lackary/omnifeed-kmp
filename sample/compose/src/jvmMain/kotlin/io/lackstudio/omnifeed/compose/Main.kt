package io.lackstudio.omnifeed.compose

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import co.touchlab.kermit.Logger
import com.mmk.kmpauth.core.KMPAuthInternalApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import io.lackstudio.omnifeed.compose.config.BuildKonfig
import io.lackstudio.omnifeed.compose.di.initKoin
import io.lackstudio.omnifeed.compose.helper.AppInitializer
import io.lackstudio.omnifeed.compose.ui.screen.App
import io.lackstudio.omnifeed.ui.component.webview.initKCEF

@OptIn(KMPAuthInternalApi::class)
fun main() = application {
    System.setProperty("PID", ProcessHandle.current().pid().toString())
    AppInitializer.onApplicationStart(BuildKonfig.GOOGLE_SERVICES_WEB_CLIENT_ID)

    val globalLogger = Logger.withTag("OmniHub")
    val appHttpClient = HttpClient(CIO) {
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

    Window(
        onCloseRequest = ::exitApplication,
        title = "Example App",
    ) {
        initKCEF {
            App()
        }
    }
}
