package io.lackstudio.omnifeed.compose

import androidx.compose.ui.window.ComposeUIViewController
import io.lackstudio.omnifeed.compose.di.viewModelModule
import io.lackstudio.omnifeed.compose.platform.getUnsplashAccessKey
import io.lackstudio.omnifeed.compose.ui.screen.App
import io.lackstudio.omnifeed.core.di.coreModule
import io.lackstudio.omnifeed.core.di.initializeKoin
import io.lackstudio.omnifeed.unsplash.di.unsplashModule
import io.lackstudio.omnifeed.unsplash.utils.Environment.AUTH_SCHEME_PUBLIC
import platform.UIKit.UIViewController

@Suppress("unused", "FunctionName")
fun MainViewController(): UIViewController {
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
    return ComposeUIViewController { App() }
}
