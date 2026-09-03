package io.lackstudio.omnifeed.unsplash.data.remote.api

import io.lackstudio.omnifeed.unsplash.di.BaseUnsplashTest
import io.lackstudio.omnifeed.unsplash.network.MOCK_COLLECTION_ID
import io.lackstudio.omnifeed.unsplash.network.MOCK_PHOTO_ID
import io.lackstudio.omnifeed.unsplash.network.MOCK_QUERY
import io.lackstudio.omnifeed.unsplash.network.MOCK_TOPIC_ID_OR_SLUG
import io.lackstudio.omnifeed.unsplash.network.MOCK_USERNAME
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UnsplashApiServiceImplTest : BaseUnsplashTest() {

    /**
     * getUserPublicProfile Test
     */
    @Test
    fun `getUserPublicProfile should return a single user's public profile by username with the correct Authorization header`() =
        runUnsplashTest {
            val user = unsplashApiService.getUserPublicProfile(MOCK_USERNAME)

            assertEquals(MOCK_USERNAME, user.username)
        }

    /**
     * getUserPhotos Test
     */
    @Test
    fun `getUserPhotos should return a user's photo list with the correct Authorization header`() =
        runUnsplashTest {
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
        runUnsplashTest {
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
        runUnsplashTest {
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
        runUnsplashTest {
            val pageSize = 10
            val photos = unsplashApiService.getPhotos(page = 1, perPage = pageSize)

            assertEquals(pageSize, photos.size)
        }

    /**
     * getPhoto Test
     */
    @Test
    fun `getPhoto should return a single photo by id with the correct Authorization header`() =
        runUnsplashTest {
            val photo = unsplashApiService.getPhoto(id = MOCK_PHOTO_ID)

            assertNotNull(photo, "Photo should be not present null")
            assertEquals(MOCK_PHOTO_ID, photo.id)
        }

    /**
     * searchPhotos Test
     */
    @Test
    fun `searchPhotos should return a photo list by query word with the correct Authorization header`() =
        runUnsplashTest {
            val pageSize = 10
            val searchPhotos = unsplashApiService.searchPhotos(query = MOCK_QUERY, page = 1, perPage = pageSize)

            assertEquals(pageSize, searchPhotos.results.size)
        }

    /**
     * searchCollections Test
     */
    @Test
    fun `searchCollections should return a collection list by query with the correct Authorization header`() =
        runUnsplashTest {
            val pageSize = 10
            val searchCollections = unsplashApiService.searchCollections(query = MOCK_QUERY, page = 1, perPage = pageSize)

            assertEquals(pageSize, searchCollections.results.size)
        }

    /**
     * searchUsers Test
     */
    @Test
    fun `searchUsers should return a user list by query with the correct Authorization header`() =
        runUnsplashTest {
            val pageSize = 10
            val searchUsers = unsplashApiService.searchUsers(query = MOCK_QUERY, page = 1, perPage = pageSize)

            assertEquals(pageSize, searchUsers.results.size)
        }

    /**
     * getCollections Test
     */
    @Test
    fun `getCollections should return a collection list with the correct Authorization header`() =
        runUnsplashTest {
            val pageSize = 10
            val collections = unsplashApiService.getCollections(page = 1, perPage = pageSize)

            assertEquals(pageSize, collections.size)
        }

    /**
     * getCollection Test
     */
    @Test
    fun `getCollection should return a single collection by id with the correct Authorization header`() =
        runUnsplashTest {
            val collection = unsplashApiService.getCollection(MOCK_COLLECTION_ID)

            assertEquals(MOCK_COLLECTION_ID,collection.id)
        }

    /**
     * getCollectionPhotos Test
     */
    @Test
    fun `getCollectionPhotos should return a collection's photos by id with the correct Authorization header`() =
        runUnsplashTest {
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
        runUnsplashTest {
            val pageSize = 10
            val topics = unsplashApiService.getTopics(page = 1, perPage = pageSize)

            assertEquals(pageSize, topics.size)
        }
    @Test
    fun `getTopic should return a topic by id or slug with the correct Authorization header`() =
        runUnsplashTest {
            val topic = unsplashApiService.getTopic(MOCK_TOPIC_ID_OR_SLUG)

            assertEquals(MOCK_TOPIC_ID_OR_SLUG, topic.slug)
        }

    @Test
    fun `getTopicPhotos should return a topic photo list by id or slug with the correctAuthorization header`() =
        runUnsplashTest {
            val pageSize = 10
            val topicPhotos = unsplashApiService.getTopicPhotos(idOrSlug = MOCK_TOPIC_ID_OR_SLUG, page = 1, perPage = pageSize)

            assertEquals(pageSize, topicPhotos.size)
            assertNotNull(topicPhotos[0].topicSubmissions.wallpapers)
        }
}
