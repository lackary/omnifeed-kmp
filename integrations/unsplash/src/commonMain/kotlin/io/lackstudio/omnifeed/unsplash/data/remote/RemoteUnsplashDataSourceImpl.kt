package io.lackstudio.omnifeed.unsplash.data.remote

import io.lackstudio.omnifeed.unsplash.data.api.UnsplashApiService
import io.lackstudio.omnifeed.unsplash.data.model.response.CollectionResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.MeProfileResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.PhotoDetailResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.SearchResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.TokenResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.TopicResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.UserProfileResponse
import io.lackstudio.omnifeed.unsplash.data.model.scheme.PhotoScheme
import io.lackstudio.omnifeed.unsplash.domain.model.OAuthCode
import io.lackstudio.omnifeed.unsplash.mapper.toTokenRequest

class RemoteUnsplashDataSourceImpl(
    private val unsplashApiService: UnsplashApiService,
) : RemoteUnsplashDataSource {

    override suspend fun getMe(): Result<MeProfileResponse> {
        return toUnsplashResult { unsplashApiService.getMe() }
    }

    override suspend fun getUserPublicProfile(username: String): Result<UserProfileResponse> {
        return toUnsplashResult { unsplashApiService.getUserPublicProfile(username) }
    }

    override suspend fun getUserPhotos(
        username: String,
        page: Int,
        perPage: Int,
        orderBy: String?,
        stats: Boolean?,
        quantity: Int?,
        orientation: String?
    ): Result<List<PhotoScheme>> {
        return toUnsplashResult {
            unsplashApiService.getUserPhotos(
                username = username,
                page = page,
                perPage = perPage,
                orderBy = orderBy,
                stats = stats,
                quantity = quantity,
                orientation = orientation
            )
        }
    }

    override suspend fun getUserLikedPhotos(
        username: String,
        page: Int,
        perPage: Int,
        orderBy: String?,
        orientation: String?
    ): Result<List<PhotoScheme>> {
        return toUnsplashResult {
            unsplashApiService.getUserLikedPhotos(
                username = username,
                page = page,
                perPage = perPage,
                orderBy = orderBy,
                orientation = orientation
            )
        }
    }

    override suspend fun getUserCollections(
        username: String,
        page: Int,
        perPage: Int
    ): Result<List<CollectionResponse>> {
        return toUnsplashResult {
            unsplashApiService.getUserCollections(
                username = username,
                page = page,
                perPage = perPage
            )
        }
    }

    override suspend fun getPhotos(page: Int, perPage: Int): Result<List<PhotoScheme>> {
        return toUnsplashResult { unsplashApiService.getPhotos(page, perPage) }
    }

    override suspend fun getPhoto(id: String): Result<PhotoDetailResponse> {
        return toUnsplashResult { unsplashApiService.getPhoto(id) }
    }

    override suspend fun searchPhotos(
        query: String,
        page: Int,
        perPage: Int,
        orderBy: String?,
        collections: String?,
        contentFilter: String?,
        color: String?,
        orientation: String?
    ): Result<SearchResponse<PhotoScheme>> {
        return toUnsplashResult {
            unsplashApiService.searchPhotos(
                query = query,
                page = page,
                perPage = perPage,
                orderBy = orderBy,
                collections = collections,
                contentFilter = contentFilter,
                color = color,
                orientation = orientation
            )
        }
    }

    override suspend fun searchCollections(
        query: String,
        page: Int,
        perPage: Int
    ): Result<SearchResponse<CollectionResponse>> {
        return toUnsplashResult { unsplashApiService.searchCollections(query, page, perPage) }
    }

    override suspend fun searchUsers(
        query: String,
        page: Int,
        perPage: Int
    ): Result<SearchResponse<UserProfileResponse>> {
        return toUnsplashResult { unsplashApiService.searchUsers(query, page, perPage) }
    }

    override suspend fun getCollections(page: Int, perPage: Int): Result<List<CollectionResponse>> {
        return toUnsplashResult { unsplashApiService.getCollections(page, perPage) }
    }

    override suspend fun getCollection(id: String): Result<CollectionResponse> {
        return toUnsplashResult { unsplashApiService.getCollection(id) }
    }

    override suspend fun getCollectionPhotos(
        id: String,
        page: Int,
        perPage: Int,
        orientation: String?
    ): Result<List<PhotoScheme>> {
        return toUnsplashResult {
            unsplashApiService.getCollectionPhotos(
                id = id,
                page = page,
                perPage = perPage,
                orientation = orientation
            )
        }
    }

    override suspend fun getCollectionRelatedCollections(id: String): Result<List<CollectionResponse>> {
        return toUnsplashResult { unsplashApiService.getCollectionRelatedCollections(id) }
    }

    override suspend fun getTopics(page: Int, perPage: Int): Result<List<TopicResponse>> {
        return toUnsplashResult { unsplashApiService.getTopics(page, perPage) }
    }

    override suspend fun getTopic(idOrSlug: String): Result<TopicResponse> {
        return toUnsplashResult { unsplashApiService.getTopic(idOrSlug) }
    }

    override suspend fun getTopicPhotos(
        idOrSlug: String,
        page: Int,
        perPage: Int,
        orientation: String?,
        orderBy: String?
    ): Result<List<PhotoScheme>> {
        return toUnsplashResult {
            unsplashApiService.getTopicPhotos(
                idOrSlug = idOrSlug,
                page = page,
                perPage = perPage,
                orientation = orientation,
                orderBy = orderBy
            )
        }
    }

    override suspend fun exchangeOAuth(oAuthCode:OAuthCode): Result<TokenResponse> {
        return toUnsplashResult {
            val unsplashTokenRequest = oAuthCode.toTokenRequest()
            unsplashApiService.postOauthToken(unsplashTokenRequest)
        }
    }
}
