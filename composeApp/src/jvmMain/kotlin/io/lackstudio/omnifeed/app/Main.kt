package io.lackstudio.omnifeed.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mmk.kmpauth.core.KMPAuthInternalApi
import io.lackstudio.omnifeed.app.config.BuildKonfig
import io.lackstudio.omnifeed.app.di.viewModelModule
import io.lackstudio.omnifeed.app.helper.AppInitializer
import io.lackstudio.omnifeed.app.platform.getUnsplashAccessKey
import io.lackstudio.omnifeed.app.ui.screen.App
import io.lackstudio.omnifeed.core.common.util.appPlatformLogWriter
import io.lackstudio.omnifeed.core.di.appLoggerModule
import io.lackstudio.omnifeed.core.di.initializeKoin
import io.lackstudio.omnifeed.ui.component.webview.initKCEF
import io.lackstudio.omnifeed.unsplash.di.unsplashModule
import io.lackstudio.omnifeed.unsplash.utils.Environment.AUTH_SCHEME_PUBLIC

@OptIn(KMPAuthInternalApi::class)
fun main() = application {
    AppInitializer.onApplicationStart(BuildKonfig.GOOGLE_SERVICES_WEB_CLIENT_ID)

    initializeKoin(
        allModules = listOf(
            appLoggerModule(appPlatformLogWriter()),
            unsplashModule(tokenType = AUTH_SCHEME_PUBLIC, token = getUnsplashAccessKey()),
            viewModelModule
        )
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "Example App",
    ) {
        initKCEF {
            App()
        }
    }
}
