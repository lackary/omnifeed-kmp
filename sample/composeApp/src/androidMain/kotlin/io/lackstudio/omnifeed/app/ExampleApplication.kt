package io.lackstudio.omnifeed.app

import android.app.Application
import io.lackstudio.omnifeed.app.di.viewModelModule
import io.lackstudio.omnifeed.app.helper.AppInitializer
import io.lackstudio.omnifeed.app.platform.getUnsplashAccessKey
import io.lackstudio.omnifeed.core.common.util.appPlatformLogWriter
import io.lackstudio.omnifeed.core.di.appLoggerModule
import io.lackstudio.omnifeed.core.di.initializeKoin
import io.lackstudio.omnifeed.unsplash.di.unsplashModule
import io.lackstudio.omnifeed.unsplash.utils.Environment.AUTH_SCHEME_PUBLIC

class ExampleApplication : Application() {
    // init the modules of DI
    override fun onCreate() {
        super.onCreate()
        initializeKoin(
            allModules = listOf(
                appLoggerModule(appPlatformLogWriter()),
                unsplashModule(tokenType = AUTH_SCHEME_PUBLIC, token = getUnsplashAccessKey()),
                viewModelModule,
            )
        )

        AppInitializer.onApplicationStart(applicationContext.getString(R.string.default_web_client_id))

    }
}
