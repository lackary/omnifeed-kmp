package io.lackstudio.omnifeed.core.di

import co.touchlab.kermit.Logger
import io.lackstudio.omnifeed.core.common.logging.LogConfiguration
import io.lackstudio.omnifeed.core.common.logging.createOmniFeedLogger
import org.koin.dsl.module

// Define Config, allowing the App to optionally pass its own Logger when initializing Koin
data class OmniFeedConfig(
    val appLogger: Logger? = null
)

internal fun loggerModule(config: OmniFeedConfig = OmniFeedConfig()) = module {
    // --- Global main Logger (for App logic and Library internal use) ---
    single<Logger> {
        // If the App provides a Config, use the App's; otherwise, create the Library default (with OmniFeedFormatter)
        config.appLogger ?: createOmniFeedLogger(LogConfiguration.OMNIFEED_TAG)
    }
}

fun coreModule(config: OmniFeedConfig = OmniFeedConfig()) = module {
    includes(loggerModule(config))
}
