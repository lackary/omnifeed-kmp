package io.lackstudio.omnifeed.core.di

import co.touchlab.kermit.Logger
import io.lackstudio.omnifeed.core.OmniFeedConfig
import io.lackstudio.omnifeed.core.common.logging.LogConfiguration
import io.lackstudio.omnifeed.core.common.logging.createOmniFeedLogger
import org.koin.dsl.module

fun loggerModule(customLogger: Logger?) = module {
    // Create a Logger instance that conforms to the project format
    val baseLogger = customLogger ?: createOmniFeedLogger(LogConfiguration.OMNIFEED_TAG)

    // This allows any call to Logger.withTag() anywhere in the project to include your Formatter
    Logger.setLogWriters(baseLogger.config.logWriterList)
    Logger.setMinSeverity(baseLogger.config.minSeverity)

    single<Logger> { baseLogger }
}

fun coreModule(config: OmniFeedConfig) = module {
    single { config }
    includes(loggerModule(config.appLogger))
}
