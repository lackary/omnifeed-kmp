package io.lackstudio.omnifeed.unsplash.data.repository

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.http.HttpStatusCode
import io.lackstudio.omnifeed.core.common.logging.setupKermitLogger
import io.lackstudio.omnifeed.core.di.KTOR_LOGGER_TAG
import io.lackstudio.omnifeed.core.network.KtorConfig
import io.lackstudio.omnifeed.core.network.oauth.AccessTokenProvider
import io.lackstudio.omnifeed.unsplash.data.api.UnsplashApiService
import io.lackstudio.omnifeed.unsplash.data.api.UnsplashApiServiceImpl
import io.lackstudio.omnifeed.unsplash.data.error.UnsplashApiException
import io.lackstudio.omnifeed.unsplash.data.local.LocalUnsplashPhotoDataSource
import io.lackstudio.omnifeed.unsplash.data.model.response.PhotoDetailResponse
import io.lackstudio.omnifeed.unsplash.data.remote.RemoteUnsplashDataSource
import io.lackstudio.omnifeed.unsplash.data.remote.RemoteUnsplashDataSourceImpl
import io.lackstudio.omnifeed.unsplash.di.BaseKoinTest
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository
import io.lackstudio.omnifeed.unsplash.network.MOCK_COLLECTION_ID
import io.lackstudio.omnifeed.unsplash.network.MOCK_ID_NOT_FOUND
import io.lackstudio.omnifeed.unsplash.network.MOCK_PHOTO_ID
import io.lackstudio.omnifeed.unsplash.network.MOCK_QUERY
import io.lackstudio.omnifeed.unsplash.network.MOCK_TOPIC_ID_OR_SLUG
import io.lackstudio.omnifeed.unsplash.network.MOCK_USERNAME
import io.lackstudio.omnifeed.unsplash.network.UnsplashMockEngine
import io.lackstudio.omnifeed.unsplash.platform.getUnsplashAccessKey
import io.lackstudio.omnifeed.unsplash.utils.Environment
import kotlinx.coroutines.test.runTest
import org.koin.dsl.module
import org.koin.test.inject
import kotlin.collections.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UnsplashRepositoryImplTest : BaseKoinTest() {
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

    private val unsplashRepository: UnsplashRepository by inject()

    @Test
    fun `getUserPublicProfile should return a user profile`() = runTest {
        val username = MOCK_USERNAME
        val userProfile = unsplashRepository.getUserPublicProfile(username)
        assertEquals(username, userProfile.username)
    }

    @Test
    fun `getUserPublicProfile should throw UnsplashApiException for a 404 response`() = runTest {
        val exception = assertFailsWith<UnsplashApiException> {
            unsplashRepository.getUserPublicProfile(MOCK_ID_NOT_FOUND)
        }
        assertEquals(HttpStatusCode.NotFound.value, exception.originalApiException.code)
    }

    @Test
    fun `getUserPhotos should return a list of photos`() = runTest {
        val pageSize = 10
        val photos = unsplashRepository.getUserPhotos(
            username = MOCK_USERNAME, page = 1, perPage = pageSize, stats = true
        )
        assertEquals(pageSize, photos.size)
    }

    @Test
    fun `getUserPhotos should throw UnsplashApiException for a 404 response`() = runTest {
        val exception = assertFailsWith<UnsplashApiException> {
            unsplashRepository.getUserPhotos(username = MOCK_ID_NOT_FOUND, page = 1, perPage = 10)
        }
        assertEquals(HttpStatusCode.NotFound.value, exception.originalApiException.code)
    }

    @Test
    fun `getUserLikedPhotos should return a list of photos`() = runTest {
        val pageSize = 10
        val photos = unsplashRepository.getUserLikedPhotos(
            username = MOCK_USERNAME, page = 1, perPage = pageSize
        )
        assertEquals(pageSize, photos.size)
    }

    @Test
    fun `getUserLikedPhotos should throw UnsplashApiException for a 404 response`() = runTest {
        val exception = assertFailsWith<UnsplashApiException> {
            unsplashRepository.getUserLikedPhotos(username = MOCK_ID_NOT_FOUND, page = 1, perPage = 10)
        }
        assertEquals(HttpStatusCode.NotFound.value, exception.originalApiException.code)
    }

    @Test
    fun `getUserCollections should return a list of collections`() = runTest {
        val pageSize = 8
        val collections = unsplashRepository.getUserCollections(
            username = MOCK_USERNAME, page = 1, perPage = pageSize
        )
        assertEquals(pageSize, collections.size)
    }

    @Test
    fun `getUserCollections should throw UnsplashApiException for a 404 response`() = runTest {
        val exception = assertFailsWith<UnsplashApiException> {
            unsplashRepository.getUserCollections(username = MOCK_ID_NOT_FOUND, page = 1, perPage = 8)
        }
        assertEquals(HttpStatusCode.NotFound.value, exception.originalApiException.code)
    }

    @Test
    fun `getPhotos should return a list of photos`() = runTest {
        val pageSize = 10
        val photos = unsplashRepository.getPhotos(page = 1, perPage = pageSize)
        assertEquals(pageSize, photos.size)
    }

    @Test
    fun `getPhoto should return a photo`() = runTest {
        val photoId = MOCK_PHOTO_ID
        val photo = unsplashRepository.getPhoto(photoId)
        assertEquals(photoId, photo.id)
    }

    @Test
    fun `getPhoto should throw UnsplashApiException for a 404 response`() = runTest {
        val exception = assertFailsWith<UnsplashApiException> {
            unsplashRepository.getPhoto(MOCK_ID_NOT_FOUND)
        }
        assertEquals(HttpStatusCode.NotFound.value, exception.originalApiException.code)
    }

    @Test
    fun `searchPhotos should return a list of photos`() = runTest {
        val pageSize = 10
        val result = unsplashRepository.searchPhotos(
            query = MOCK_QUERY, page = 1, perPage = pageSize
        )
        assertEquals(pageSize, result.results.size)
    }

    @Test
    fun `searchCollections should return a list of collections`() = runTest {
        val pageSize = 10
        val result = unsplashRepository.searchCollections(
            query = MOCK_QUERY, page = 1, perPage = pageSize
        )
        assertEquals(pageSize, result.results.size)
    }

    @Test
    fun `searchUsers should return a list of users`() = runTest {
        val pageSize = 10
        val result = unsplashRepository.searchUsers(
            query = MOCK_QUERY, page = 1, perPage = pageSize
        )
        assertEquals(pageSize, result.results.size)
    }

    @Test
    fun `getCollections should return a list of collections`() = runTest {
        val pageSize = 10
        val collections = unsplashRepository.getCollections(page = 1, perPage = pageSize)
        assertEquals(pageSize, collections.size)
    }

    @Test
    fun `getCollection should return a collection`() = runTest {
        val collectionId = MOCK_COLLECTION_ID
        val collection = unsplashRepository.getCollection(collectionId)
        assertEquals(collectionId, collection.id)
    }

    @Test
    fun `getCollection should throw UnsplashApiException for a 404 response`() = runTest {
        val exception = assertFailsWith<UnsplashApiException> {
            unsplashRepository.getCollection(MOCK_ID_NOT_FOUND)
        }
        assertEquals(HttpStatusCode.NotFound.value, exception.originalApiException.code)
    }

    @Test
    fun `getCollectionPhotos should return a list of photos`() = runTest {
        val pageSize = 10
        val photos = unsplashRepository.getCollectionPhotos(
            id = MOCK_COLLECTION_ID, page = 1, perPage = pageSize
        )
        assertEquals(pageSize, photos.size)
    }

    @Test
    fun `getTopics should return a list of topics`() = runTest {
        val pageSize = 10
        val topics = unsplashRepository.getTopics(page = 1, perPage = pageSize)
        assertEquals(pageSize, topics.size)
    }

    @Test
    fun `getTopic should return a topic`() = runTest {
        val topic = unsplashRepository.getTopic(MOCK_TOPIC_ID_OR_SLUG)
        assertEquals(MOCK_TOPIC_ID_OR_SLUG, topic.slug)
    }

    @Test
    fun `getTopicPhotos should return a list of photos`() = runTest {
        val pageSize = 10
        val photos = unsplashRepository.getTopicPhotos(
            idOrSlug = MOCK_TOPIC_ID_OR_SLUG, page = 1, perPage = pageSize
        )
        assertEquals(pageSize, photos.size)
        assertNotNull(photos[0].topicSubmissions.wallpapers)
    }
}
