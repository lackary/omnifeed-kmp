package io.lackstudio.omnifeed.compose

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mmk.kmpauth.core.KMPAuthInternalApi
import io.lackstudio.omnifeed.compose.config.BuildKonfig
import io.lackstudio.omnifeed.compose.di.viewModelModule
import io.lackstudio.omnifeed.compose.helper.AppInitializer
import io.lackstudio.omnifeed.compose.platform.getUnsplashAccessKey
import io.lackstudio.omnifeed.compose.ui.screen.App
import io.lackstudio.omnifeed.core.di.coreModule
import io.lackstudio.omnifeed.core.di.initializeKoin
import io.lackstudio.omnifeed.ui.component.webview.initKCEF
import io.lackstudio.omnifeed.unsplash.di.unsplashModule
import io.lackstudio.omnifeed.unsplash.utils.Environment.AUTH_SCHEME_PUBLIC

@OptIn(KMPAuthInternalApi::class)
fun main() = application {
    System.setProperty("PID", ProcessHandle.current().pid().toString())
    AppInitializer.onApplicationStart(BuildKonfig.GOOGLE_SERVICES_WEB_CLIENT_ID)

    initializeKoin(
        allModules = listOf(
            coreModule(),
            unsplashModule(
                tokenType = AUTH_SCHEME_PUBLIC,
                token = getUnsplashAccessKey()
            ),
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
