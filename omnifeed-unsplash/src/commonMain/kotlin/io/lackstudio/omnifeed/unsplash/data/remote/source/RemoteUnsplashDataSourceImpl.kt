package io.lackstudio.omnifeed.unsplash.data.remote.source

import co.touchlab.kermit.Logger
import io.lackstudio.omnifeed.unsplash.data.remote.api.UnsplashApiService
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.CollectionResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.MeProfileResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.PhotoDetailResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.SearchResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.TokenResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.TopicResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.UserProfileResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PhotoDto
import io.lackstudio.omnifeed.unsplash.domain.model.OAuthCode
import io.lackstudio.omnifeed.unsplash.mapper.toTokenRequest

class RemoteUnsplashDataSourceImpl(
    private val unsplashApiService: UnsplashApiService,
) : RemoteUnsplashDataSource {

    private val logger = Logger.withTag("RemoteUnsplashDataSource")

    override suspend fun getMe(): Result<MeProfileResponse> {
        logger.d { "getMe" }
        return toUnsplashResult { unsplashApiService.getMe() }
    }

    override suspend fun getUserPublicProfile(username: String): Result<UserProfileResponse> {
        logger.d { "getUserPublicProfile: $username" }
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
    ): Result<List<PhotoDto>> {
        logger.d { "getUserPhotos: $username, page: $page" }
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
    ): Result<List<PhotoDto>> {
        logger.d { "getUserLikedPhotos: $username, page: $page" }
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
        logger.d { "getUserCollections: $username, page: $page" }
        return toUnsplashResult {
            unsplashApiService.getUserCollections(
                username = username,
                page = page,
                perPage = perPage
            )
        }
    }

    override suspend fun getPhotos(page: Int, perPage: Int): Result<List<PhotoDto>> {
        logger.d { "getPhotos: page: $page" }
        return toUnsplashResult { unsplashApiService.getPhotos(page, perPage) }
    }

    override suspend fun getPhoto(id: String): Result<PhotoDetailResponse> {
        logger.d { "getPhoto: id: $id" }
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
    ): Result<SearchResponse<PhotoDto>> {
        logger.d { "searchPhotos: query: $query, page: $page" }
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
        logger.d { "searchCollections: query: $query, page: $page" }
        return toUnsplashResult { unsplashApiService.searchCollections(query, page, perPage) }
    }

    override suspend fun searchUsers(
        query: String,
        page: Int,
        perPage: Int
    ): Result<SearchResponse<UserProfileResponse>> {
        logger.d { "searchUsers: query: $query, page: $page" }
        return toUnsplashResult { unsplashApiService.searchUsers(query, page, perPage) }
    }

    override suspend fun getCollections(page: Int, perPage: Int): Result<List<CollectionResponse>> {
        logger.d { "getCollections: page: $page" }
        return toUnsplashResult { unsplashApiService.getCollections(page, perPage) }
    }

    override suspend fun getCollection(id: String): Result<CollectionResponse> {
        logger.d { "getCollection: id: $id" }
        return toUnsplashResult { unsplashApiService.getCollection(id) }
    }

    override suspend fun getCollectionPhotos(
        id: String,
        page: Int,
        perPage: Int,
        orientation: String?
    ): Result<List<PhotoDto>> {
        logger.d { "getCollectionPhotos: id: $id, page: $page" }
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
        logger.d { "getCollectionRelatedCollections: id: $id" }
        return toUnsplashResult { unsplashApiService.getCollectionRelatedCollections(id) }
    }

    override suspend fun getTopics(page: Int, perPage: Int): Result<List<TopicResponse>> {
        logger.d { "getTopics: page: $page" }
        return toUnsplashResult { unsplashApiService.getTopics(page, perPage) }
    }

    override suspend fun getTopic(idOrSlug: String): Result<TopicResponse> {
        logger.d { "getTopic: id: $idOrSlug" }
        return toUnsplashResult { unsplashApiService.getTopic(idOrSlug) }
    }

    override suspend fun getTopicPhotos(
        idOrSlug: String,
        page: Int,
        perPage: Int,
        orientation: String?,
        orderBy: String?
    ): Result<List<PhotoDto>> {
        logger.d { "getTopicPhotos: id: $idOrSlug, page: $page" }
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
        logger.i { "exchangeOAuth" }
        return toUnsplashResult {
            val unsplashTokenRequest = oAuthCode.toTokenRequest()
            unsplashApiService.postOauthToken(unsplashTokenRequest)
        }
    }
}
