package io.lackstudio.omnifeed.app

import androidx.compose.ui.window.ComposeUIViewController
import io.lackstudio.omnifeed.app.di.viewModelModule
import io.lackstudio.omnifeed.app.platform.getUnsplashAccessKey
import io.lackstudio.omnifeed.app.ui.screen.App
import io.lackstudio.omnifeed.core.common.util.appPlatformLogWriter
import io.lackstudio.omnifeed.core.di.appLoggerModule
import io.lackstudio.omnifeed.core.di.initializeKoin
import io.lackstudio.omnifeed.unsplash.di.unsplashModule
import io.lackstudio.omnifeed.unsplash.utils.Environment.AUTH_SCHEME_PUBLIC
import platform.UIKit.UIViewController

@Suppress("unused", "FunctionName")
fun MainViewController(): UIViewController {
    initializeKoin(
        allModules = listOf(
            appLoggerModule(appPlatformLogWriter()),
            unsplashModule(tokenType = AUTH_SCHEME_PUBLIC, token = getUnsplashAccessKey()),
            viewModelModule
        )
    )
    return ComposeUIViewController { App() }
}
