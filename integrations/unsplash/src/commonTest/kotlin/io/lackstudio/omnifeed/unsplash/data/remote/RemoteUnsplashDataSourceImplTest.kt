package io.lackstudio.omnifeed.unsplash.data.remote

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.logging.LogLevel
import io.lackstudio.omnifeed.unsplash.data.api.UnsplashApiService
import io.lackstudio.omnifeed.unsplash.data.api.UnsplashApiServiceImpl
import io.lackstudio.omnifeed.unsplash.di.BaseKoinTest
import io.ktor.http.HttpStatusCode
import io.lackstudio.omnifeed.core.common.logging.setupKermitLogger
import io.lackstudio.omnifeed.core.di.KTOR_LOGGER_TAG
import io.lackstudio.omnifeed.core.network.KtorConfig
import io.lackstudio.omnifeed.core.network.oauth.AccessTokenProvider
import io.lackstudio.omnifeed.unsplash.data.error.UnsplashApiException
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RemoteUnsplashDataSourceImplTest: BaseKoinTest() {
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
            single<UnsplashApiService> { UnsplashApiServiceImpl (get(), get()) }
            single<RemoteUnsplashDataSource> { RemoteUnsplashDataSourceImpl(get()) }
        }
    )

    private val remoteUnsplashDataSource: RemoteUnsplashDataSource by inject()

    @Test
    fun `getUserPublicProfile should return a successful Result with a user profile`() =
        runTest {
            val username = MOCK_USERNAME
            val result = remoteUnsplashDataSource.getUserPublicProfile(username)
            assertTrue(result.isSuccess)
            val user = result.getOrThrow()
            assertNotNull(user)
            assertEquals(username, user.username)
        }

    @Test
    fun `getUserPublicProfile should return a failure Result with NotFound AppException for a 404 response`() =
        runTest {
            val result = remoteUnsplashDataSource.getUserPublicProfile(MOCK_ID_NOT_FOUND)
            assertTrue(result.isFailure)
            val appException = result.exceptionOrNull()
            assertTrue(appException is UnsplashApiException)
            assertEquals(
                HttpStatusCode.NotFound.value,
                appException.originalApiException.code
            )
        }

    @Test
    fun `getUserPhotos should return a successful Result with a list of photos`() =
        runTest {
            val pageSize = 10
            val stats = true
            val result = remoteUnsplashDataSource.getUserPhotos(
                username = MOCK_USERNAME, page = 1, perPage = pageSize, stats = stats
            )
            assertTrue(result.isSuccess)
            val userPhotos = result.getOrThrow()
            assertEquals(pageSize, userPhotos.size)
        }

    @Test
    fun `getUserPhotos should return a failure Result with NotFound AppException for a 404 response`() =
        runTest {
            val result = remoteUnsplashDataSource.getUserPhotos(
                username = MOCK_ID_NOT_FOUND, page = 1, perPage = 10
            )
            assertTrue(result.isFailure)
            val appException = result.exceptionOrNull()
            assertTrue(appException is UnsplashApiException)
            assertEquals(
                HttpStatusCode.NotFound.value,
                appException.originalApiException.code
            )
        }

    @Test
    fun `getUserLikedPhotos should return a successful Result with a list of photos`() =
        runTest {
            val pageSize = 10
            val result = remoteUnsplashDataSource.getUserLikedPhotos(
                username = MOCK_USERNAME, page = 1, perPage = pageSize
            )
            assertTrue(result.isSuccess)
            val likedPhotos = result.getOrThrow()
            assertEquals(pageSize, likedPhotos.size)
        }

    @Test
    fun `getUserLikedPhotos should return a failure Result with NotFound AppException for a 404 response`() =
        runTest {
            val result = remoteUnsplashDataSource.getUserLikedPhotos(
                username = MOCK_ID_NOT_FOUND, page = 1, perPage = 10
            )
            assertTrue(result.isFailure)
            val appException = result.exceptionOrNull()
            assertTrue(appException is UnsplashApiException)
            assertEquals(
                HttpStatusCode.NotFound.value,
                appException.originalApiException.code
            )
        }

    @Test
    fun `getUserCollections should return a successful Result with a list of collections`() =
        runTest {
            val pageSize = 8
            val result = remoteUnsplashDataSource.getUserCollections(
                username = MOCK_USERNAME, page = 1, perPage = pageSize
            )
            assertTrue(result.isSuccess)
            val userCollections = result.getOrThrow()
            assertEquals(pageSize, userCollections.size)
        }

    @Test
    fun `getUserCollections should return a failure Result with NotFound AppException for a 404 response`() =
        runTest {
            val result = remoteUnsplashDataSource.getUserCollections(
                username = MOCK_ID_NOT_FOUND, page = 1, perPage = 8
            )
            assertTrue(result.isFailure)
            val appException = result.exceptionOrNull()
            assertTrue(appException is UnsplashApiException)
            assertEquals(
                HttpStatusCode.NotFound.value,
                appException.originalApiException.code
            )
        }

    @Test
    fun `getPhotos should return a successful Result with a list of photos`() =
        runTest {
            val pageSize = 10
            val result = remoteUnsplashDataSource.getPhotos(page = 1, perPage = pageSize)
            assertTrue(result.isSuccess)
            val photos = result.getOrThrow()
            assertEquals(pageSize, photos.size)
        }

    @Test
    fun `getPhoto should return a successful Result with a photo`() =
        runTest {
            val photoId = MOCK_PHOTO_ID
            val result = remoteUnsplashDataSource.getPhoto(photoId)
            assertTrue(result.isSuccess)
            val photo = result.getOrThrow()
            assertNotNull(photo)
            assertEquals(photoId, photo.id)
        }

    @Test
    fun `getPhoto should return a failure Result with NotFound AppException for a 404 response`() =
        runTest {
            val result = remoteUnsplashDataSource.getPhoto(MOCK_ID_NOT_FOUND)
            assertTrue(result.isFailure)
            val appException = result.exceptionOrNull()
            assertTrue(appException is UnsplashApiException)
            assertEquals(
                HttpStatusCode.NotFound.value,
                appException.originalApiException.code
            )
        }

    @Test
    fun `searchPhotos should return a successful Result with a list of photos`() =
        runTest {
            val pageSize = 10
            val result = remoteUnsplashDataSource.searchPhotos(
                query = MOCK_QUERY, page = 1, perPage = pageSize
            )
            assertTrue(result.isSuccess)
            val searchPhotos = result.getOrThrow()
            assertEquals(pageSize, searchPhotos.results.size)
        }

    @Test
    fun `searchCollections should return a successful Result with a list of collections`() =
        runTest {
            val pageSize = 10
            val result = remoteUnsplashDataSource.searchCollections(
                query = MOCK_QUERY, page = 1, perPage = pageSize
            )
            assertTrue(result.isSuccess)
            val searchCollections = result.getOrThrow()
            assertEquals(pageSize, searchCollections.results.size)
        }

    @Test
    fun `searchUsers should return a successful Result with a list of users`() =
        runTest {
            val pageSize = 10
            val result = remoteUnsplashDataSource.searchUsers(
                query = MOCK_QUERY, page = 1, perPage = pageSize
            )
            assertTrue(result.isSuccess)
            val searchUsers = result.getOrThrow()
            assertEquals(pageSize, searchUsers.results.size)
        }

    @Test
    fun `getCollections should return a successful Result with a list of collections`() =
        runTest {
            val pageSize = 10
            val result = remoteUnsplashDataSource.getCollections(page = 1, perPage = pageSize)
            assertTrue(result.isSuccess)
            val collections = result.getOrThrow()
            assertEquals(pageSize, collections.size)
        }

    @Test
    fun `getCollection should return a successful Result with a collection`() =
        runTest {
            val collectionId = MOCK_COLLECTION_ID
            val result = remoteUnsplashDataSource.getCollection(collectionId)
            assertTrue(result.isSuccess)
            val collection = result.getOrThrow()
            assertNotNull(collection)
            assertEquals(collectionId, collection.id)
        }

    @Test
    fun `getCollection should return a failure Result with NotFound AppException for a 404 response`() =
        runTest {
            val result = remoteUnsplashDataSource.getCollection(MOCK_ID_NOT_FOUND)
            assertTrue(result.isFailure)
            val appException = result.exceptionOrNull()
            assertTrue(appException is UnsplashApiException)
            assertEquals(
                HttpStatusCode.NotFound.value,
                appException.originalApiException.code
            )
        }

    @Test
    fun `getCollectionPhotos should return a successful Result with a list of photos`() =
        runTest {
            val pageSize = 10
            val result = remoteUnsplashDataSource.getCollectionPhotos(
                id = MOCK_COLLECTION_ID, page = 1, perPage = pageSize
            )
            assertTrue(result.isSuccess)
            val collectionPhotos = result.getOrThrow()
            assertEquals(pageSize, collectionPhotos.size)
        }

    @Test
    fun `getTopics should return a successful Result with a list of topics`() =
        runTest {
            val pageSize = 10
            val result = remoteUnsplashDataSource.getTopics(page = 1, perPage = pageSize)
            assertTrue(result.isSuccess)
            val topics = result.getOrThrow()
            assertEquals(pageSize, topics.size)
        }

    @Test
    fun `getTopic should return a successful Result with a topic`() =
        runTest {
            val result = remoteUnsplashDataSource.getTopic(MOCK_TOPIC_ID_OR_SLUG)
            assertTrue(result.isSuccess)
            val topic = result.getOrThrow()
            assertEquals(MOCK_TOPIC_ID_OR_SLUG, topic.slug)
        }

    @Test
    fun `getTopicPhotos should return a successful Result with a list of photos`() =
        runTest {
            val pageSize = 10
            val result = remoteUnsplashDataSource.getTopicPhotos(
                idOrSlug = MOCK_TOPIC_ID_OR_SLUG, page = 1, perPage = pageSize
            )
            assertTrue(result.isSuccess)
            val topicPhotos = result.getOrThrow()
            assertEquals(pageSize, topicPhotos.size)
            assertNotNull(topicPhotos[0].topicSubmissions.wallpapers)
        }
}
