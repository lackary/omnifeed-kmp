package io.lackstudio.omnifeed.auth.di

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeConfig
import io.lackstudio.omnifeed.auth.data.local.source.AuthLocalDataSource
import io.lackstudio.omnifeed.auth.data.local.source.AuthLocalDataSourceImpl
import io.lackstudio.omnifeed.auth.data.storage.KSafeLocalStorage
import io.lackstudio.omnifeed.auth.data.storage.LocalStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val authLocalModule = module {
    // Firestore User Storage
    single<LocalStorage>(namedUserCacheStorage) {
        KSafeLocalStorage(
            KSafe(
                context = androidContext(),
                fileName = FILENAME_OMNIFEED_AUTH_FIREBASE_TOKEN,
                config = KSafeConfig(appNamespace = CONFIG_MODULE_NAMESPACE_OMNIFEED_AUTH)
            )
        )
    }

    // Service Token Storage
    single<LocalStorage>(namedServiceTokenStorage) {
        KSafeLocalStorage(
            KSafe(
                context = androidContext(),
                fileName = FILENAME_OMNIFEED_AUTH_SERVICE_TOKEN,
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
