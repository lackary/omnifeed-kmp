package io.lackstudio.module.kmp.apiclient.unsplash.data.api

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.logging.LogLevel
import io.lackstudio.module.kmp.apiclient.core.common.logging.setupKermitLogger
import io.lackstudio.module.kmp.apiclient.core.di.KTOR_LOGGER_TAG
import io.lackstudio.module.kmp.apiclient.core.network.KtorConfig
import io.lackstudio.module.kmp.apiclient.core.network.oauth.AccessTokenProvider
import io.lackstudio.module.kmp.apiclient.unsplash.di.BaseKoinTest
import io.lackstudio.module.kmp.apiclient.unsplash.network.MOCK_COLLECTION_ID
import io.lackstudio.module.kmp.apiclient.unsplash.network.MOCK_PHOTO_ID
import io.lackstudio.module.kmp.apiclient.unsplash.network.MOCK_QUERY
import io.lackstudio.module.kmp.apiclient.unsplash.network.MOCK_TOPIC_ID_OR_SLUG
import io.lackstudio.module.kmp.apiclient.unsplash.network.MOCK_USERNAME
import io.lackstudio.module.kmp.apiclient.unsplash.network.UnsplashMockEngine
import io.lackstudio.module.kmp.apiclient.unsplash.platform.provideHttpClientEngineTest
import io.lackstudio.module.kmp.apiclient.unsplash.platform.getUnsplashAccessKey
import io.lackstudio.module.kmp.apiclient.unsplash.utils.Environment
import org.koin.dsl.module
import org.koin.test.inject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import kotlin.test.assertNotNull

class UnsplashApiServiceImplTest: BaseKoinTest() {

    override val appLogWriter: LogWriter
        get() = platformLogWriter()
    override val engine: HttpClientEngine
        get() = UnsplashMockEngine
//        get() = provideHttpClientEngineTest()
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
        }
    )

    private val unsplashApiService: UnsplashApiService by inject()

    /**
     * getUserPublicProfile Test
     */
    @Test
    fun `getUserPublicProfile should return a single user's public profile by username with the correct Authorization header`() =
        runTest {
            val user = unsplashApiService.getUserPublicProfile(MOCK_USERNAME)

            assertEquals(MOCK_USERNAME, user.username)
        }

    /**
     * getUserPhotos Test
     */
    @Test
    fun `getUserPhotos should return a user's photo list with the correct Authorization header`() =
        runTest {
            val pageSize = 10
            val stats = true
            val userPhotos =
                unsplashApiService.getUserPhotos(
                    username = MOCK_USERNAME, page = 1, perPage = pageSize, stats = stats
                )

            assertEquals(pageSize, userPhotos.size)
        }

    /**
     * getUserLikedPhotos Test
     */
    @Test
    fun `getUserLikedPhotos should return a user's liked Photos with the Authorization header`() =
        runTest {
            val pageSize = 10
            val likedPhotos =
                unsplashApiService.getUserLikedPhotos(
                    username = MOCK_USERNAME, page = 1, perPage = pageSize
                )

            assertEquals(pageSize, likedPhotos.size)
        }

    /**
     * getUserCollections Test
     */
    @Test
    fun `getUserCollections should return a user collections`() =
        runTest {
            val pageSize = 8
            val userCollections =
                unsplashApiService.getUserCollections(
                    username = MOCK_USERNAME, page = 1, perPage = pageSize
                )

            assertEquals(pageSize, userCollections.size)
        }

    /**
     * getPhotos Test
     */
    @Test
    fun `getPhotos should return a photo list with the correct Authorization header`() =
        runTest {
            val pageSize = 10
            val photos = unsplashApiService.getPhotos(page = 1, perPage = pageSize)

            assertEquals(pageSize, photos.size)
        }

    /**
     * getPhoto Test
     */
    @Test
    fun `getPhoto should return a single photo by id with the correct Authorization header`() =
        runTest {
            val photo = unsplashApiService.getPhoto(id = MOCK_PHOTO_ID)

            assertNotNull(photo, "Photo should be not present null")
            assertEquals(MOCK_PHOTO_ID, photo.id)
        }

    /**
     * searchPhotos Test
     */
    @Test
    fun `searchPhotos should return a photo list by query word with the correct Authorization header`() =
        runTest {
            val pageSize = 10
            val searchPhotos = unsplashApiService.searchPhotos(query = MOCK_QUERY, page = 1, perPage = pageSize)

            assertEquals(pageSize, searchPhotos.results.size)
        }

    /**
     * searchCollections Test
     */
    @Test
    fun `searchCollections should return a collection list by query with the correct Authorization header`() =
        runTest {
            val pageSize = 10
            val searchCollections = unsplashApiService.searchCollections(query = MOCK_QUERY, page = 1, perPage = pageSize)

            assertEquals(pageSize, searchCollections.results.size)
        }

    /**
     * searchUsers Test
     */
    @Test
    fun `searchUsers should return a user list by query with the correct Authorization header`() =
        runTest {
            val pageSize = 10
            val searchUsers = unsplashApiService.searchUsers(query = MOCK_QUERY, page = 1, perPage = pageSize)

            assertEquals(pageSize, searchUsers.results.size)
        }

    /**
     * getCollections Test
     */
    @Test
    fun `getCollections should return a collection list with the correct Authorization header`() =
        runTest {
            val pageSize = 10
            val collections = unsplashApiService.getCollections(page = 1, perPage = pageSize)

            assertEquals(pageSize, collections.size)
        }

    /**
     * getCollection Test
     */
    @Test
    fun `getCollection should return a single collection by id with the correct Authorization header`() =
        runTest {
            val collection = unsplashApiService.getCollection(MOCK_COLLECTION_ID)

            assertEquals(MOCK_COLLECTION_ID,collection.id)
        }

    /**
     * getCollectionPhotos Test
     */
    @Test
    fun `getCollectionPhotos should return a collection's photos by id with the correct Authorization header`() =
        runTest {
            val pageSize = 10
            val collectionPhotos =
                unsplashApiService.getCollectionPhotos(MOCK_COLLECTION_ID, page = 1, perPage = pageSize)

            assertEquals(pageSize, collectionPhotos.size)
        }

//    @Test
//    fun `getCollectionRelatedCollections should return a collection's related collections`() =
//            runTest {
//            val pageSize = 10
//            val  collectionRelatedCollections =
//                unsplashApiService.getCollectionRelatedCollections(id = collectionId)
//
//            assertEquals(pageSize, collectionRelatedCollections.size)
//        }

    @Test
    fun `getTopics should return a topic list with the correct Authorization header` () =
        runTest {
            val pageSize = 10
            val topics = unsplashApiService.getTopics(page = 1, perPage = pageSize)

            assertEquals(pageSize, topics.size)
        }
    @Test
    fun `getTopic should return a topic by id or slug with the correct Authorization header`() =
        runTest {
            val topic = unsplashApiService.getTopic(MOCK_TOPIC_ID_OR_SLUG)

            assertEquals(MOCK_TOPIC_ID_OR_SLUG, topic.slug)
        }

    @Test
    fun `getTopicPhotos should return a topic photo list by id or slug with the correctAuthorization header`() =
        runTest {
            val pageSize = 10
            val topicPhotos = unsplashApiService.getTopicPhotos(idOrSlug = MOCK_TOPIC_ID_OR_SLUG, page = 1, perPage = pageSize)

            assertEquals(pageSize, topicPhotos.size)
            assertNotNull(topicPhotos[0].topicSubmissions.wallpapers)
        }
}
