package io.lackstudio.omnifeed.unsplash.di

import io.lackstudio.omnifeed.core.common.logging.KtorKermitLogger
import io.lackstudio.omnifeed.core.common.logging.setupKermitLogger
import io.lackstudio.omnifeed.core.common.util.appPlatformLogWriter
import io.lackstudio.omnifeed.core.network.KtorClientFactory
import io.lackstudio.omnifeed.core.network.KtorConfig
import io.lackstudio.omnifeed.core.network.oauth.AccessTokenProvider
import io.lackstudio.omnifeed.core.network.provideHttpClientEngine
import io.lackstudio.omnifeed.unsplash.data.api.UnsplashApiService
import io.lackstudio.omnifeed.unsplash.data.api.UnsplashApiServiceImpl
import io.lackstudio.omnifeed.unsplash.data.local.LocalUnsplashPhotoDataSource
import io.lackstudio.omnifeed.unsplash.data.local.LocalUnsplashDataSourceImpl
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository
import io.lackstudio.omnifeed.unsplash.data.repository.UnsplashRepositoryImpl
import io.lackstudio.omnifeed.unsplash.data.remote.RemoteUnsplashDataSource
import io.lackstudio.omnifeed.unsplash.data.remote.RemoteUnsplashDataSourceImpl
import io.lackstudio.omnifeed.unsplash.domain.usecase.ExchangeOAuthUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetCollectionPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetCollectionRelatedCollectionsUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetCollectionUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetMeUseCase
import io.lackstudio.omnifeed.unsplash.utils.Environment
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetPhotoUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetCollectionsUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetTopicPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetTopicUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetTopicsUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserCollectionsUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserLikedPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserPublicProfileUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.SearchCollectionsUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.SearchPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.SearchUsersUseCase
import org.koin.dsl.module

const val TAG_UNSPLASH_KTOR = "UnsplashKtor"

fun unsplashModule(tokenType: String, token: String) = module {
    single {
        AccessTokenProvider(
            initialTokenType = tokenType,
            initialToken = token
        )
    }
    val ktorConfig = KtorConfig(
        baseUrl = Environment.BASE_API_URL,
    )
    val kermitLogger = setupKermitLogger(
        tag = TAG_UNSPLASH_KTOR,
        logWriter = appPlatformLogWriter()
    )

    single {
        KtorClientFactory.createHttpClient(
            engineFactory = provideHttpClientEngine(),
            ktorConfig = ktorConfig,
            logger = KtorKermitLogger(kermitLogger),
            accessTokenProvider = { get() }
        )
    }
    single<UnsplashApiService> {
        UnsplashApiServiceImpl(
            get(),
            get()
        )
    }
    single<LocalUnsplashPhotoDataSource> { LocalUnsplashDataSourceImpl(/* inject Room DAO */) }
    single<RemoteUnsplashDataSource> {
        RemoteUnsplashDataSourceImpl(
            get()
        )
    }
    single<UnsplashRepository> {
        UnsplashRepositoryImpl(
            get(),
            get()
        )
    }

    factory { GetPhotosUseCase(get()) }
    factory { GetPhotoUseCase(get()) }
    factory { GetCollectionsUseCase(get()) }
    factory { GetCollectionUseCase(get()) }
    factory { GetCollectionPhotosUseCase(get()) }
    factory { GetCollectionRelatedCollectionsUseCase(get()) }
    factory { GetTopicsUseCase(get()) }
    factory { GetTopicUseCase(get()) }
    factory { GetTopicPhotosUseCase(get()) }
    factory { GetUserPublicProfileUseCase(get()) }
    factory { GetUserPhotosUseCase(get()) }
    factory { GetUserLikedPhotosUseCase(get()) }
    factory { GetUserCollectionsUseCase(get()) }
    factory { SearchPhotosUseCase(get()) }
    factory { SearchCollectionsUseCase(get()) }
    factory { SearchUsersUseCase(get()) }

    factory {
        ExchangeOAuthUseCase(
            get()
        )
    }
    factory { GetMeUseCase(get()) }
}
