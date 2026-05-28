package io.lackstudio.omnifeed.unsplash.di

import co.touchlab.kermit.platformLogWriter
import io.ktor.client.plugins.logging.LogLevel
import io.lackstudio.omnifeed.core.common.logging.LogConfiguration.OMNIFEED_KTOR_TAG
import io.lackstudio.omnifeed.core.common.logging.createOmniFeedLogger
import io.lackstudio.omnifeed.core.network.KtorClientFactory
import io.lackstudio.omnifeed.core.network.KtorConfig
import io.lackstudio.omnifeed.core.network.oauth.AccessTokenProvider
import io.lackstudio.omnifeed.unsplash.data.remote.api.UnsplashApiService
import io.lackstudio.omnifeed.unsplash.data.remote.api.UnsplashApiServiceImpl
import io.lackstudio.omnifeed.unsplash.data.local.source.LocalUnsplashPhotoDataSource
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.PhotoDetailResponse
import io.lackstudio.omnifeed.unsplash.data.remote.source.RemoteUnsplashDataSource
import io.lackstudio.omnifeed.unsplash.data.remote.source.RemoteUnsplashDataSourceImpl
import io.lackstudio.omnifeed.unsplash.data.repository.UnsplashRepositoryImpl
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository
import io.lackstudio.omnifeed.unsplash.network.UnsplashMockEngine
import io.lackstudio.omnifeed.unsplash.platform.getUnsplashAccessKey
import io.lackstudio.omnifeed.unsplash.utils.Environment
import kotlinx.coroutines.test.runTest

// 1. Create an environment class to "independently load" all objects required for a single test
class UnsplashTestEnvironment {
    val engine = UnsplashMockEngine

    private val ktorConfig = KtorConfig(
        baseUrl = Environment.BASE_API_URL,
        logLevel = LogLevel.ALL,
        connectTimeoutMillis = null,
        requestTimeoutMillis = null,
        socketTimeoutMillis = null
    )
    private val accessTokenProvider = AccessTokenProvider(
        initialTokenType = Environment.AUTH_SCHEME_PUBLIC,
        initialToken = getUnsplashAccessKey()
    )
    private val kermitLogger = createOmniFeedLogger(tag = OMNIFEED_KTOR_TAG, logWriter = platformLogWriter())

    val client = KtorClientFactory.createHttpClient(
        engineFactory = engine,
        ktorConfig = ktorConfig,
        logger = io.lackstudio.omnifeed.core.common.logging.KtorKermitLogger(kermitLogger),
        accessTokenProvider = { accessTokenProvider }
    )

    // Dependencies provided for direct access by external tests
    val unsplashApiService: UnsplashApiService = UnsplashApiServiceImpl(client, kermitLogger)
    val remoteUnsplashDataSource: RemoteUnsplashDataSource = RemoteUnsplashDataSourceImpl(unsplashApiService)

    private val localDataSource = object : LocalUnsplashPhotoDataSource {
        override suspend fun getPhoto(id: String): PhotoDetailResponse? = null
        override suspend fun savePhoto(photo: PhotoDetailResponse) {}
    }

    val unsplashRepository: UnsplashRepository = UnsplashRepositoryImpl(remoteUnsplashDataSource, localDataSource)

    // Method to close resources
    fun close() {
        client.close()
        engine.close()
    }
}

abstract class BaseUnsplashTest {

    // 2. Define a custom runUnsplashTest to replace the official runTest
    // This way, a fresh environment is created internally for each test and automatically closed at the end
    protected fun runUnsplashTest(block: suspend UnsplashTestEnvironment.() -> Unit) = runTest {
        val env = UnsplashTestEnvironment()
        try {
            env.block() // Execute your test code
        } finally {
            env.close() // End of test, ensures Ktor background coroutines are cleaned up!
        }
    }
}
