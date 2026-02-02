package io.lackstudio.omnifeed.unsplash.data.api

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.lackstudio.omnifeed.unsplash.data.model.request.TokenRequest
import io.lackstudio.omnifeed.unsplash.data.model.response.CollectionResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.MeProfileResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.PhotoDetailResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.SearchResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.TokenResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.TopicResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.UserProfileResponse
import io.lackstudio.omnifeed.unsplash.data.model.scheme.PhotoScheme
import io.lackstudio.omnifeed.unsplash.utils.Environment
import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys

class UnsplashApiServiceImpl(
    private val httpClient: HttpClient,
    private val logger: Logger
) : UnsplashApiService {

    override suspend fun getMe(): MeProfileResponse {
        logger.d { "getMe" }
        return httpClient.get(Environment.API_ME).body()
    }

    override suspend fun getUserPublicProfile(username: String): UserProfileResponse {
        logger.d { "getUserPublicProfile username: $username" }
        return httpClient.get("${Environment.API_USERS}/$username").body()
    }

    override suspend fun getUserPhotos(
        username: String,
        page: Int,
        perPage: Int,
        orderBy: String?,
        stats: Boolean?,
        quantity: Int?,
        orientation: String?
    ): List<PhotoScheme> {
        logger.d { "getUserPhotos username: $username, page: $page" }
        return httpClient.get("${Environment.API_USERS}/$username${Environment.API_PHOTOS}") {
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
            parameter(ApiKeys.Params.ORDER_BY, orderBy)
            parameter(ApiKeys.Params.STATS, stats)
            parameter(ApiKeys.Params.QUANTITY, quantity)
            parameter(ApiKeys.Params.ORIENTATION, orientation)
        }.body()
    }

    override suspend fun getUserLikedPhotos(
        username: String,
        page: Int,
        perPage: Int,
        orderBy: String?,
        orientation: String?
    ): List<PhotoScheme> {
        logger.d { "getUserLikedPhotos username: $username, page: $page" }
        return httpClient.get("${Environment.API_USERS}/$username${Environment.API_LIKES}") {
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
            parameter(ApiKeys.Params.ORDER_BY, orderBy)
            parameter(ApiKeys.Params.ORIENTATION, orientation)
        }.body()
    }

    override suspend fun getUserCollections(
        username: String,
        page: Int,
        perPage: Int
    ): List<CollectionResponse> {
        logger.d { "getUserCollections username: $username, page: $page" }
        return httpClient.get("${Environment.API_USERS}/$username${Environment.API_COLLECTIONS}") {
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
        }.body()
    }

    override suspend fun getCollectionPhotos(
        id: String,
        page: Int,
        perPage: Int,
        orientation: String?
    ): List<PhotoScheme> {
        logger.d { "getCollectionPhotos id: $id, page: $page" }
        return httpClient.get("${Environment.API_COLLECTIONS}/$id${Environment.API_PHOTOS}") {
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
            parameter(ApiKeys.Params.ORIENTATION, orientation)
        }.body()
    }

    override suspend fun getCollectionRelatedCollections(id: String): List<CollectionResponse> {
        logger.d { "getCollectionRelatedCollections id: $id" }
        return httpClient.get("${Environment.API_COLLECTIONS}/$id${Environment.API_RELATED}").body()
    }

    override suspend fun getPhotos(
        page: Int,
        perPage: Int
    ): List<PhotoScheme> {
        logger.d { "getPhotos page: $page, perPage: $perPage" }
        return httpClient.get(Environment.API_PHOTOS) {
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
        }.body()
    }

    override suspend fun getPhoto(id: String): PhotoDetailResponse {
        logger.d { "getPhoto id: $id" }
        return httpClient.get("${Environment.API_PHOTOS}/$id").body()
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
    ): SearchResponse<PhotoScheme> {
        logger.d { "searchPhotos query: $query, page: $page" }
        return httpClient.get("${Environment.API_SEARCH}${Environment.API_PHOTOS}") {
            parameter(ApiKeys.Params.QUERY, query)
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
            parameter(ApiKeys.Params.ORDER_BY, orderBy)
            parameter(ApiKeys.Params.COLLECTIONS, collections)
            parameter(ApiKeys.Params.CONTENT_FILTER, contentFilter)
            parameter(ApiKeys.Params.COLOR, color)
            parameter(ApiKeys.Params.ORIENTATION, orientation)
        }.body()
    }

    override suspend fun searchCollections(
        query: String,
        page: Int,
        perPage: Int
    ): SearchResponse<CollectionResponse> {
        logger.d { "searchCollections query: $query, page: $page" }
        return httpClient.get("${Environment.API_SEARCH}${Environment.API_COLLECTIONS}") {
            parameter(ApiKeys.Params.QUERY, query)
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
        }.body()
    }

    override suspend fun searchUsers(
        query: String,
        page: Int,
        perPage: Int
    ): SearchResponse<UserProfileResponse> {
        logger.d { "searchUsers query: $query, page: $page" }
        return httpClient.get("${Environment.API_SEARCH}${Environment.API_USERS}") {
            parameter(ApiKeys.Params.QUERY, query)
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
        }.body()
    }

    override suspend fun getCollections(page: Int, perPage: Int): List<CollectionResponse> {
        logger.d { "getCollections page: $page, perPage: $perPage" }
        return httpClient.get(Environment.API_COLLECTIONS) {
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
        }.body()
    }

    override suspend fun getCollection(id: String): CollectionResponse {
        logger.d { "getCollection id: $id" }
        return httpClient.get("${Environment.API_COLLECTIONS}/$id").body()
    }

    override suspend fun getTopics(page: Int, perPage: Int): List<TopicResponse> {
        logger.d { "getTopics page: $page, perPage: $perPage" }
        return httpClient.get(Environment.API_TOPICS) {
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
        }.body()
    }

    override suspend fun getTopic(idOrSlug: String): TopicResponse {
        logger.d { "getTopic id: $idOrSlug" }
        return httpClient.get("${Environment.API_TOPICS}/$idOrSlug").body()
    }

    override suspend fun getTopicPhotos(
        idOrSlug: String,
        page: Int,
        perPage: Int,
        orientation: String?,
        orderBy: String?
    ): List<PhotoScheme> {
        logger.d { "getTopicPhotos id: $idOrSlug, page: $page" }
        return httpClient.get("${Environment.API_TOPICS}/$idOrSlug${Environment.API_PHOTOS}") {
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
            parameter(ApiKeys.Params.ORIENTATION, orientation)
            parameter(ApiKeys.Params.ORDER_BY, orderBy)
        }.body()
    }

    override suspend fun postOauthToken(
        unsplashTokenRequest: TokenRequest
    ): TokenResponse {
        logger.d { "postOauthToken" }
        return httpClient.post(urlString = Environment.OAUTH_TOKEN) {
            setBody(unsplashTokenRequest)
        }.body()
    }
}
