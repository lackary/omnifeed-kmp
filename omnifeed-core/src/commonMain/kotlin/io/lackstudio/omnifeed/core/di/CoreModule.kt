package io.lackstudio.omnifeed.core.di

import co.touchlab.kermit.Logger
import io.lackstudio.omnifeed.core.OmniFeedConfig
import io.lackstudio.omnifeed.core.common.logging.LogConfiguration
import io.lackstudio.omnifeed.core.common.logging.createOmniFeedLogger
import org.koin.dsl.module

fun loggerModule(customLogger: Logger?) = module {
    // --- Global main Logger (for App logic and Library internal use) ---
    single<Logger> {
        // If the App provides a Config, use the App's; otherwise, create the Library default (with OmniFeedFormatter)
        customLogger ?: createOmniFeedLogger(LogConfiguration.OMNIFEED_TAG)
    }
}

fun coreModule(config: OmniFeedConfig) = module {
    includes(loggerModule(config.appLogger))
}
