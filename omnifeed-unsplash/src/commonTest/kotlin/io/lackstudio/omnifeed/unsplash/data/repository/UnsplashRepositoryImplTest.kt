package io.lackstudio.omnifeed.unsplash.data.repository

import io.ktor.http.HttpStatusCode
import io.lackstudio.omnifeed.unsplash.data.error.UnsplashApiException
import io.lackstudio.omnifeed.unsplash.di.BaseUnsplashTest
import io.lackstudio.omnifeed.unsplash.network.MOCK_COLLECTION_ID
import io.lackstudio.omnifeed.unsplash.network.MOCK_ID_NOT_FOUND
import io.lackstudio.omnifeed.unsplash.network.MOCK_PHOTO_ID
import io.lackstudio.omnifeed.unsplash.network.MOCK_QUERY
import io.lackstudio.omnifeed.unsplash.network.MOCK_TOPIC_ID_OR_SLUG
import io.lackstudio.omnifeed.unsplash.network.MOCK_USERNAME
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UnsplashRepositoryImplTest : BaseUnsplashTest() {

    @Test
    fun `getUserPublicProfile should return a user profile`() = runUnsplashTest {
        val username = MOCK_USERNAME
        val userProfile = unsplashRepository.getUserPublicProfile(username)
        assertEquals(username, userProfile.username)
    }

    @Test
    fun `getUserPublicProfile should throw UnsplashApiException for a 404 response`() = runUnsplashTest {
        val exception = assertFailsWith<UnsplashApiException> {
            unsplashRepository.getUserPublicProfile(MOCK_ID_NOT_FOUND)
        }
        assertEquals(HttpStatusCode.NotFound.value, exception.originalApiException.code)
    }

    @Test
    fun `getUserPhotos should return a list of photos`() = runUnsplashTest {
        val pageSize = 10
        val photos = unsplashRepository.getUserPhotos(
            username = MOCK_USERNAME, page = 1, perPage = pageSize, stats = true
        )
        assertEquals(pageSize, photos.size)
    }

    @Test
    fun `getUserPhotos should throw UnsplashApiException for a 404 response`() = runUnsplashTest {
        val exception = assertFailsWith<UnsplashApiException> {
            unsplashRepository.getUserPhotos(username = MOCK_ID_NOT_FOUND, page = 1, perPage = 10)
        }
        assertEquals(HttpStatusCode.NotFound.value, exception.originalApiException.code)
    }

    @Test
    fun `getUserLikedPhotos should return a list of photos`() = runUnsplashTest {
        val pageSize = 10
        val photos = unsplashRepository.getUserLikedPhotos(
            username = MOCK_USERNAME, page = 1, perPage = pageSize
        )
        assertEquals(pageSize, photos.size)
    }

    @Test
    fun `getUserLikedPhotos should throw UnsplashApiException for a 404 response`() = runUnsplashTest {
        val exception = assertFailsWith<UnsplashApiException> {
            unsplashRepository.getUserLikedPhotos(username = MOCK_ID_NOT_FOUND, page = 1, perPage = 10)
        }
        assertEquals(HttpStatusCode.NotFound.value, exception.originalApiException.code)
    }

    @Test
    fun `getUserCollections should return a list of collections`() = runUnsplashTest {
        val pageSize = 8
        val collections = unsplashRepository.getUserCollections(
            username = MOCK_USERNAME, page = 1, perPage = pageSize
        )
        assertEquals(pageSize, collections.size)
    }

    @Test
    fun `getUserCollections should throw UnsplashApiException for a 404 response`() = runUnsplashTest {
        val exception = assertFailsWith<UnsplashApiException> {
            unsplashRepository.getUserCollections(username = MOCK_ID_NOT_FOUND, page = 1, perPage = 8)
        }
        assertEquals(HttpStatusCode.NotFound.value, exception.originalApiException.code)
    }

    @Test
    fun `getPhotos should return a list of photos`() = runUnsplashTest {
        val pageSize = 10
        val photos = unsplashRepository.getPhotos(page = 1, perPage = pageSize)
        assertEquals(pageSize, photos.size)
    }

    @Test
    fun `getPhoto should return a photo`() = runUnsplashTest {
        val photoId = MOCK_PHOTO_ID
        val photo = unsplashRepository.getPhoto(photoId)
        assertEquals(photoId, photo.id)
    }

    @Test
    fun `getPhoto should throw UnsplashApiException for a 404 response`() = runUnsplashTest {
        val exception = assertFailsWith<UnsplashApiException> {
            unsplashRepository.getPhoto(MOCK_ID_NOT_FOUND)
        }
        assertEquals(HttpStatusCode.NotFound.value, exception.originalApiException.code)
    }

    @Test
    fun `searchPhotos should return a list of photos`() = runUnsplashTest {
        val pageSize = 10
        val result = unsplashRepository.searchPhotos(
            query = MOCK_QUERY, page = 1, perPage = pageSize
        )
        assertEquals(pageSize, result.results.size)
    }

    @Test
    fun `searchCollections should return a list of collections`() = runUnsplashTest {
        val pageSize = 10
        val result = unsplashRepository.searchCollections(
            query = MOCK_QUERY, page = 1, perPage = pageSize
        )
        assertEquals(pageSize, result.results.size)
    }

    @Test
    fun `searchUsers should return a list of users`() = runUnsplashTest {
        val pageSize = 10
        val result = unsplashRepository.searchUsers(
            query = MOCK_QUERY, page = 1, perPage = pageSize
        )
        assertEquals(pageSize, result.results.size)
    }

    @Test
    fun `getCollections should return a list of collections`() = runUnsplashTest {
        val pageSize = 10
        val collections = unsplashRepository.getCollections(page = 1, perPage = pageSize)
        assertEquals(pageSize, collections.size)
    }

    @Test
    fun `getCollection should return a collection`() = runUnsplashTest {
        val collectionId = MOCK_COLLECTION_ID
        val collection = unsplashRepository.getCollection(collectionId)
        assertEquals(collectionId, collection.id)
    }

    @Test
    fun `getCollection should throw UnsplashApiException for a 404 response`() = runUnsplashTest {
        val exception = assertFailsWith<UnsplashApiException> {
            unsplashRepository.getCollection(MOCK_ID_NOT_FOUND)
        }
        assertEquals(HttpStatusCode.NotFound.value, exception.originalApiException.code)
    }

    @Test
    fun `getCollectionPhotos should return a list of photos`() = runUnsplashTest {
        val pageSize = 10
        val photos = unsplashRepository.getCollectionPhotos(
            id = MOCK_COLLECTION_ID, page = 1, perPage = pageSize
        )
        assertEquals(pageSize, photos.size)
    }

    @Test
    fun `getTopics should return a list of topics`() = runUnsplashTest {
        val pageSize = 10
        val topics = unsplashRepository.getTopics(page = 1, perPage = pageSize)
        assertEquals(pageSize, topics.size)
    }

    @Test
    fun `getTopic should return a topic`() = runUnsplashTest {
        val topic = unsplashRepository.getTopic(MOCK_TOPIC_ID_OR_SLUG)
        assertEquals(MOCK_TOPIC_ID_OR_SLUG, topic.slug)
    }

    @Test
    fun `getTopicPhotos should return a list of photos`() = runUnsplashTest {
        val pageSize = 10
        val photos = unsplashRepository.getTopicPhotos(
            idOrSlug = MOCK_TOPIC_ID_OR_SLUG, page = 1, perPage = pageSize
        )
        assertEquals(pageSize, photos.size)
        assertNotNull(photos[0].topicSubmissions.wallpapers)
    }
}
