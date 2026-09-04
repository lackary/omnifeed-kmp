package io.lackstudio.omnifeed.auth.di

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeConfig
import io.lackstudio.omnifeed.auth.data.local.source.AuthLocalDataSource
import io.lackstudio.omnifeed.auth.data.local.source.AuthLocalDataSourceImpl
import io.lackstudio.omnifeed.auth.data.storage.KSafeLocalStorage
import io.lackstudio.omnifeed.auth.data.storage.LocalStorage
import org.koin.dsl.module

actual val authLocalModule = module {
    val os = System.getProperty("os.name").lowercase()
    val userHome = System.getProperty("user.home")

    // Select standard app data directory based on operating system
    val appDataDir = when {
        os.contains("win") ->
            java.io.File(System.getenv("AppData"), "OmniFeed")
        os.contains("mac") ->
            java.io.File(userHome, "Library/Application Support/OmniFeed")
        else ->
            java.io.File(userHome, ".omnifeed") // Linux or others
    }

    if (!appDataDir.exists()) {
        appDataDir.mkdirs()
    }

    // Firebase User Storage
    single<LocalStorage>(namedUserCacheStorage) {
        KSafeLocalStorage(
            KSafe(
                fileName = FILENAME_OMNIFEED_AUTH_FIREBASE_TOKEN,
                baseDir = appDataDir,
                config = KSafeConfig(appNamespace = CONFIG_MODULE_NAMESPACE_OMNIFEED_AUTH)
            )
        )
    }

    // Service Token Storage
    single<LocalStorage>(namedServiceTokenStorage) {
        KSafeLocalStorage(
            KSafe(
                fileName = FILENAME_OMNIFEED_AUTH_SERVICE_TOKEN,
                baseDir = appDataDir,
                config = KSafeConfig(appNamespace = CONFIG_MODULE_NAMESPACE_OMNIFEED_AUTH)
            )
        )
    }

    // AuthLocalDataSource
    single<AuthLocalDataSource> {
        AuthLocalDataSourceImpl(
            userCacheStorage = get(namedUserCacheStorage),
            serviceTokenStorage = get(namedServiceTokenStorage)
        )
    }
}
