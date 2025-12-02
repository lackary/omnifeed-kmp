package io.lackstudio.omnifeed.core.di

import co.touchlab.kermit.LogWriter
import io.ktor.client.engine.HttpClientEngine
import io.lackstudio.omnifeed.core.common.logging.AppKermitLogger
import io.lackstudio.omnifeed.core.common.logging.AppLogger
import io.lackstudio.omnifeed.core.common.logging.KtorKermitLogger
import io.lackstudio.omnifeed.core.common.logging.LogConfiguration
import io.lackstudio.omnifeed.core.common.logging.setupKermitLogger
import io.lackstudio.omnifeed.core.network.KtorClientFactory
import io.lackstudio.omnifeed.core.network.KtorConfig
import io.ktor.client.plugins.logging.Logger as KtorLogger
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val APP_LOGGER_TAG = "app_logger_tag"
const val KTOR_LOGGER_TAG = "ktor_logger_tag"

fun appLoggerModule(logWriter: LogWriter) = module {
    single(named(APP_LOGGER_TAG)) {
        setupKermitLogger(LogConfiguration.appTag, logWriter)
    }
    single<AppLogger> { AppKermitLogger(get(named(APP_LOGGER_TAG))) }
}

fun ktorLoggerModule(logWriter: LogWriter) = module {
    single(named(KTOR_LOGGER_TAG)) {
        setupKermitLogger(LogConfiguration.ktorTag, logWriter)
    }
    single<KtorLogger> { KtorKermitLogger(get(named(KTOR_LOGGER_TAG))) }

}

fun ktorClientModule(
    engineFactory: HttpClientEngine,
    ktorConfig: KtorConfig,
    logger: KtorLogger
) = module {
    single {
        KtorClientFactory.createHttpClient(
            engineFactory = engineFactory,
            ktorConfig = ktorConfig,
            logger = logger,
            accessTokenProvider = { get() }
        )
    }
}
