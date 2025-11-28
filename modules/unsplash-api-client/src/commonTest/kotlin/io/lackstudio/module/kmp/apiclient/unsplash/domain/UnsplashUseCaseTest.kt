package io.lackstudio.module.kmp.apiclient.unsplash.domain

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.logging.LogLevel
import io.lackstudio.module.kmp.apiclient.core.common.logging.setupKermitLogger
import io.lackstudio.module.kmp.apiclient.core.di.KTOR_LOGGER_TAG
import io.lackstudio.module.kmp.apiclient.core.domain.usecase.UseCaseResult
import io.lackstudio.module.kmp.apiclient.core.network.KtorConfig
import io.lackstudio.module.kmp.apiclient.core.network.oauth.AccessTokenProvider
import io.lackstudio.module.kmp.apiclient.unsplash.data.api.UnsplashApiService
import io.lackstudio.module.kmp.apiclient.unsplash.data.api.UnsplashApiServiceImpl
import io.lackstudio.module.kmp.apiclient.unsplash.data.local.LocalUnsplashPhotoDataSource
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.PhotoDetailResponse
import io.lackstudio.module.kmp.apiclient.unsplash.data.remote.RemoteUnsplashDataSource
import io.lackstudio.module.kmp.apiclient.unsplash.data.remote.RemoteUnsplashDataSourceImpl
import io.lackstudio.module.kmp.apiclient.unsplash.data.repository.UnsplashRepositoryImpl
import io.lackstudio.module.kmp.apiclient.unsplash.di.BaseKoinTest
import io.lackstudio.module.kmp.apiclient.unsplash.domain.repository.UnsplashRepository
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetCollectionPhotosParams
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetCollectionPhotosUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetCollectionRelatedCollectionsUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetCollectionUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetCollectionsParams
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetCollectionsUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetPhotoUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetPhotosParams
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetPhotosUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetTopicPhotosParams
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetTopicPhotosUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetTopicUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetTopicsParams
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetTopicsUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetUserCollectionsParams
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetUserCollectionsUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetUserLikedPhotosParams
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetUserLikedPhotosUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetUserPhotosParams
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetUserPhotosUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.GetUserPublicProfileUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.SearchCollectionsParams
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.SearchCollectionsUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.SearchPhotosParams
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.SearchPhotosUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.SearchUsersParams
import io.lackstudio.module.kmp.apiclient.unsplash.domain.usecase.SearchUsersUseCase
import io.lackstudio.module.kmp.apiclient.unsplash.network.MOCK_COLLECTION_ID
import io.lackstudio.module.kmp.apiclient.unsplash.network.MOCK_PHOTO_ID
import io.lackstudio.module.kmp.apiclient.unsplash.network.MOCK_QUERY
import io.lackstudio.module.kmp.apiclient.unsplash.network.MOCK_TOPIC_ID_OR_SLUG
import io.lackstudio.module.kmp.apiclient.unsplash.network.MOCK_USERNAME
import io.lackstudio.module.kmp.apiclient.unsplash.network.UnsplashMockEngine
import io.lackstudio.module.kmp.apiclient.unsplash.platform.getUnsplashAccessKey
import io.lackstudio.module.kmp.apiclient.unsplash.utils.Environment
import kotlinx.coroutines.test.runTest
import org.koin.dsl.module
import org.koin.test.inject
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UnsplashUseCaseTest : BaseKoinTest() {
    override val appLogWriter: LogWriter
        get() = platformLogWriter()
    override val engine: HttpClientEngine
        get() = UnsplashMockEngine
    override val ktorConfig: KtorConfig
        get() = KtorConfig(
            baseUrl = Environment.BASE_API_URL,
            logLevel = LogLevel.ALL
        )
    override val kermitLogger: Logger
        get() = setupKermitLogger(
            tag = KTOR_LOGGER_TAG,
            logWriter = platformLogWriter()
        )
    override val accessTokenProvider: AccessTokenProvider
        get() = AccessTokenProvider(
            initialTokenType = Environment.AUTH_SCHEME_PUBLIC,
            initialToken = getUnsplashAccessKey()
        )

    override val testModules = listOf(
        module {
            single<UnsplashApiService> { UnsplashApiServiceImpl(get(), get()) }
            single<RemoteUnsplashDataSource> { RemoteUnsplashDataSourceImpl(get()) }
            single<LocalUnsplashPhotoDataSource> {
                object : LocalUnsplashPhotoDataSource {
                    override suspend fun getPhoto(id: String): PhotoDetailResponse? = null
                    override suspend fun savePhoto(photo: PhotoDetailResponse) {}
                }
            }
            single<UnsplashRepository> { UnsplashRepositoryImpl(get(), get()) }
        }
    )

    private val repository: UnsplashRepository by inject()

    @Test
    fun `GetUserPublicProfileUseCase should return success`() = runTest {
        val useCase = GetUserPublicProfileUseCase(repository)
        val result = useCase(MOCK_USERNAME)
        assertTrue(result is UseCaseResult.Success)
        assertEquals(MOCK_USERNAME, result.data.username)
    }

    @Test
    fun `GetUserPhotosUseCase should return success`() = runTest {
        val useCase = GetUserPhotosUseCase(repository)
        val result = useCase(GetUserPhotosParams(MOCK_USERNAME, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.size)
    }

    @Test
    fun `GetUserLikedPhotosUseCase should return success`() = runTest {
        val useCase = GetUserLikedPhotosUseCase(repository)
        val result = useCase(GetUserLikedPhotosParams(MOCK_USERNAME, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.size)
    }

    @Test
    fun `GetUserCollectionsUseCase should return success`() = runTest {
        val useCase = GetUserCollectionsUseCase(repository)
        val result = useCase(GetUserCollectionsParams(MOCK_USERNAME, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(8, result.data.size)
    }

    @Test
    fun `GetPhotosUseCase should return success`() = runTest {
        val useCase = GetPhotosUseCase(repository)
        val result = useCase(GetPhotosParams(1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.size)
    }

    @Test
    fun `GetPhotoUseCase should return success`() = runTest {
        val useCase = GetPhotoUseCase(repository)
        val result = useCase(MOCK_PHOTO_ID)
        assertTrue(result is UseCaseResult.Success)
        assertEquals(MOCK_PHOTO_ID, result.data.id)
    }

    @Test
    fun `SearchPhotosUseCase should return success`() = runTest {
        val useCase = SearchPhotosUseCase(repository)
        val result = useCase(SearchPhotosParams(MOCK_QUERY, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.results.size)
    }

    @Test
    fun `SearchCollectionsUseCase should return success`() = runTest {
        val useCase = SearchCollectionsUseCase(repository)
        val result = useCase(SearchCollectionsParams(MOCK_QUERY, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.results.size)
    }

    @Test
    fun `SearchUsersUseCase should return success`() = runTest {
        val useCase = SearchUsersUseCase(repository)
        val result = useCase(SearchUsersParams(MOCK_QUERY, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.results.size)
    }

    @Test
    fun `GetCollectionsUseCase should return success`() = runTest {
        val useCase = GetCollectionsUseCase(repository)
        val result = useCase(GetCollectionsParams(1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.size)
    }

    @Test
    fun `GetCollectionUseCase should return success`() = runTest {
        val useCase = GetCollectionUseCase(repository)
        val result = useCase(MOCK_COLLECTION_ID)
        assertTrue(result is UseCaseResult.Success)
        assertEquals(MOCK_COLLECTION_ID, result.data.id)
    }

    @Test
    fun `GetCollectionPhotosUseCase should return success`() = runTest {
        val useCase = GetCollectionPhotosUseCase(repository)
        val result = useCase(GetCollectionPhotosParams(MOCK_COLLECTION_ID, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.size)
    }

    @Ignore
    @Test
    fun `GetCollectionRelatedCollectionsUseCase should return success`() = runTest {
        val useCase = GetCollectionRelatedCollectionsUseCase(repository)
        val result = useCase(MOCK_COLLECTION_ID)
        assertTrue(result is UseCaseResult.Success)
        assertTrue(result.data.isNotEmpty())
    }

    @Test
    fun `GetTopicsUseCase should return success`() = runTest {
        val useCase = GetTopicsUseCase(repository)
        val result = useCase(GetTopicsParams(1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.size)
    }

    @Test
    fun `GetTopicUseCase should return success`() = runTest {
        val useCase = GetTopicUseCase(repository)
        val result = useCase(MOCK_TOPIC_ID_OR_SLUG)
        assertTrue(result is UseCaseResult.Success)
        assertEquals(MOCK_TOPIC_ID_OR_SLUG, result.data.slug)
    }

    @Test
    fun `GetTopicPhotosUseCase should return success`() = runTest {
        val useCase = GetTopicPhotosUseCase(repository)
        val result = useCase(GetTopicPhotosParams(MOCK_TOPIC_ID_OR_SLUG, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.size)
        assertNotNull(result.data[0].topicSubmissions.wallpapers)
    }
}
