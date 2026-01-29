package io.lackstudio.omnifeed.unsplash.di

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import io.ktor.client.engine.HttpClientEngine
import io.lackstudio.omnifeed.core.OmniFeedConfig
import io.lackstudio.omnifeed.core.UnsplashConfig
import io.lackstudio.omnifeed.core.common.logging.KtorKermitLogger
import io.lackstudio.omnifeed.core.di.coreModule
import io.lackstudio.omnifeed.core.network.KtorClientFactory
import io.lackstudio.omnifeed.core.network.KtorConfig
import io.lackstudio.omnifeed.core.network.oauth.AccessTokenProvider
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class BaseKoinTest: KoinTest {

    protected abstract val testModules: List<Module>
    protected abstract val unsplashTestLogWriter: LogWriter
    protected abstract val engine: HttpClientEngine
    protected abstract val ktorConfig: KtorConfig
    protected abstract val kermitLogger: Logger
    protected abstract val accessTokenProvider: AccessTokenProvider

    @BeforeTest
    fun setupKoin() {
        stopKoin()
        val config = OmniFeedConfig(
            appLogger = null,
            unsplash = UnsplashConfig(
                tokenType = accessTokenProvider.getOAuthToken().type,
                token = accessTokenProvider.getOAuthToken().value
            )
        )
        startKoin {
            modules(
                // api-client-unsplash need AppLogger for
                coreModule(config),
                module {
                    single {
                        // 直接呼叫 Factory 建立 Client，完全掌控參數
                        KtorClientFactory.createHttpClient(
                            engineFactory = engine,
                            ktorConfig = ktorConfig,
                            // 手動傳入測試用的 Logger (這裡就能解決參數問題)
                            logger = KtorKermitLogger(kermitLogger),
                            // 使用 Koin 裡的 AccessTokenProvider (下面那個 module 定義的)
                            accessTokenProvider = { get() }
                        )
                    }
                },
                module {
                    single {
                        accessTokenProvider
                    }
                },
                *testModules.toTypedArray()
            )
        }
    }

    @AfterTest
    fun tearDownKoin() {
        stopKoin()
    }
}
