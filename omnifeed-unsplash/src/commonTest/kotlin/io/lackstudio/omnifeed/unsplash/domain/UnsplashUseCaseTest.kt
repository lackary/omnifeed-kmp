package io.lackstudio.omnifeed.unsplash.domain

import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.lackstudio.omnifeed.unsplash.di.BaseUnsplashTest
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetCollectionPhotosParams
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetCollectionPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetCollectionRelatedCollectionsUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetCollectionUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetCollectionsParams
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetCollectionsUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetPhotoUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetPhotosParams
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetTopicPhotosParams
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetTopicPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetTopicUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetTopicsParams
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetTopicsUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserCollectionsParams
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserCollectionsUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserLikedPhotosParams
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserLikedPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserPhotosParams
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserPublicProfileUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.SearchCollectionsParams
import io.lackstudio.omnifeed.unsplash.domain.usecase.SearchCollectionsUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.SearchPhotosParams
import io.lackstudio.omnifeed.unsplash.domain.usecase.SearchPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.SearchUsersParams
import io.lackstudio.omnifeed.unsplash.domain.usecase.SearchUsersUseCase
import io.lackstudio.omnifeed.unsplash.network.MOCK_COLLECTION_ID
import io.lackstudio.omnifeed.unsplash.network.MOCK_PHOTO_ID
import io.lackstudio.omnifeed.unsplash.network.MOCK_QUERY
import io.lackstudio.omnifeed.unsplash.network.MOCK_TOPIC_ID_OR_SLUG
import io.lackstudio.omnifeed.unsplash.network.MOCK_USERNAME
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UnsplashUseCaseTest : BaseUnsplashTest() {

    @Test
    fun `GetUserPublicProfileUseCase should return success`() = runUnsplashTest {
        val useCase = GetUserPublicProfileUseCase(unsplashRepository)
        val result = useCase(MOCK_USERNAME)
        assertTrue(result is UseCaseResult.Success)
        assertEquals(MOCK_USERNAME, result.data.username)
    }

    @Test
    fun `GetUserPhotosUseCase should return success`() = runUnsplashTest {
        val useCase = GetUserPhotosUseCase(unsplashRepository)
        val result = useCase(GetUserPhotosParams(MOCK_USERNAME, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.size)
    }

    @Test
    fun `GetUserLikedPhotosUseCase should return success`() = runUnsplashTest {
        val useCase = GetUserLikedPhotosUseCase(unsplashRepository)
        val result = useCase(GetUserLikedPhotosParams(MOCK_USERNAME, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.size)
    }

    @Test
    fun `GetUserCollectionsUseCase should return success`() = runUnsplashTest {
        val useCase = GetUserCollectionsUseCase(unsplashRepository)
        val result = useCase(GetUserCollectionsParams(MOCK_USERNAME, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(8, result.data.size)
    }

    @Test
    fun `GetPhotosUseCase should return success`() = runUnsplashTest {
        val useCase = GetPhotosUseCase(unsplashRepository)
        val result = useCase(GetPhotosParams(1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.size)
    }

    @Test
    fun `GetPhotoUseCase should return success`() = runUnsplashTest {
        val useCase = GetPhotoUseCase(unsplashRepository)
        val result = useCase(MOCK_PHOTO_ID)
        assertTrue(result is UseCaseResult.Success)
        assertEquals(MOCK_PHOTO_ID, result.data.id)
    }

    @Test
    fun `SearchPhotosUseCase should return success`() = runUnsplashTest {
        val useCase = SearchPhotosUseCase(unsplashRepository)
        val result = useCase(SearchPhotosParams(MOCK_QUERY, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.results.size)
    }

    @Test
    fun `SearchCollectionsUseCase should return success`() = runUnsplashTest {
        val useCase = SearchCollectionsUseCase(unsplashRepository)
        val result = useCase(SearchCollectionsParams(MOCK_QUERY, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.results.size)
    }

    @Test
    fun `SearchUsersUseCase should return success`() = runUnsplashTest {
        val useCase = SearchUsersUseCase(unsplashRepository)
        val result = useCase(SearchUsersParams(MOCK_QUERY, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.results.size)
    }

    @Test
    fun `GetCollectionsUseCase should return success`() = runUnsplashTest {
        val useCase = GetCollectionsUseCase(unsplashRepository)
        val result = useCase(GetCollectionsParams(1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.size)
    }

    @Test
    fun `GetCollectionUseCase should return success`() = runUnsplashTest {
        val useCase = GetCollectionUseCase(unsplashRepository)
        val result = useCase(MOCK_COLLECTION_ID)
        assertTrue(result is UseCaseResult.Success)
        assertEquals(MOCK_COLLECTION_ID, result.data.id)
    }

    @Test
    fun `GetCollectionPhotosUseCase should return success`() = runUnsplashTest {
        val useCase = GetCollectionPhotosUseCase(unsplashRepository)
        val result = useCase(GetCollectionPhotosParams(MOCK_COLLECTION_ID, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.size)
    }

    @Ignore
    @Test
    fun `GetCollectionRelatedCollectionsUseCase should return success`() = runUnsplashTest {
        val useCase = GetCollectionRelatedCollectionsUseCase(unsplashRepository)
        val result = useCase(MOCK_COLLECTION_ID)
        assertTrue(result is UseCaseResult.Success)
        assertTrue(result.data.isNotEmpty())
    }

    @Test
    fun `GetTopicsUseCase should return success`() = runUnsplashTest {
        val useCase = GetTopicsUseCase(unsplashRepository)
        val result = useCase(GetTopicsParams(1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.size)
    }

    @Test
    fun `GetTopicUseCase should return success`() = runUnsplashTest {
        val useCase = GetTopicUseCase(unsplashRepository)
        val result = useCase(MOCK_TOPIC_ID_OR_SLUG)
        assertTrue(result is UseCaseResult.Success)
        assertEquals(MOCK_TOPIC_ID_OR_SLUG, result.data.slug)
    }

    @Test
    fun `GetTopicPhotosUseCase should return success`() = runUnsplashTest {
        val useCase = GetTopicPhotosUseCase(unsplashRepository)
        val result = useCase(GetTopicPhotosParams(MOCK_TOPIC_ID_OR_SLUG, 1, 10))
        assertTrue(result is UseCaseResult.Success)
        assertEquals(10, result.data.size)
        assertNotNull(result.data[0].topicSubmissions.wallpapers)
    }
}
