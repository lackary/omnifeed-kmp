package io.lackstudio.module.kmp.apiclient.unsplash.data.api

import io.lackstudio.module.kmp.apiclient.core.common.logging.AppLogger
import io.lackstudio.module.kmp.apiclient.unsplash.utils.Environment
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.request.TokenRequest
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.CollectionResponse
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.MeProfileResponse
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.PhotoDetailResponse
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.SearchResponse
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.TokenResponse
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.TopicResponse
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.UserProfileResponse
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.PhotoScheme
import io.lackstudio.module.kmp.apiclient.unsplash.utils.constants.ApiKeys

class UnsplashApiServiceImpl(
    private val httpClient: HttpClient,
    private val appLogger: AppLogger
) : UnsplashApiService {
    val TAG = "UnsplashApiServiceImpl"

    override suspend fun getMe(): MeProfileResponse {
        appLogger.debug(tag = TAG, message = "getMe")
        return httpClient.get(Environment.API_ME).body()
    }

    override suspend fun getUserPublicProfile(username: String): UserProfileResponse {
        appLogger.debug(tag = TAG, message = "getUserPublicProfile username $username")
        return httpClient.get("${Environment.API_USERS}/$username") {
            parameter(ApiKeys.Params.USERNAME, username)
        }.body()
    }

    override suspend fun getUserPhotos(
        username: String,
        page: Int,
        perPage: Int,
        orderBy: String?,
        stats: Boolean?,
//        resolution: String?,
        quantity: Int?,
        orientation: String?
    ): List<PhotoScheme> {
        return httpClient.get("${Environment.API_USERS}/$username${Environment.API_PHOTOS}") {
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
            parameter(ApiKeys.Params.ORDER_BY, orderBy)
            parameter(ApiKeys.Params.STATS, stats)
//            parameter(ApiKeys.Params.RESOLUTION, resolution)
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
        return httpClient.get("${Environment.API_COLLECTIONS}/$id${Environment.API_PHOTOS}") {
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
            parameter(ApiKeys.Params.ORIENTATION, orientation)
        }.body()
    }

    override suspend fun getCollectionRelatedCollections(id: String): List<CollectionResponse> {
        return httpClient.get("${Environment.API_COLLECTIONS}/$id${Environment.API_RELATED}").body()
    }

    override suspend fun getPhotos(
        page: Int,
        perPage: Int
    ): List<PhotoScheme> {
        appLogger.debug(tag = TAG, message = "getPhotos page $page, perPage $perPage")
        return httpClient.get(Environment.API_PHOTOS) {
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
        }.body()
    }

    override suspend fun getPhoto(id: String): PhotoDetailResponse {
        appLogger.debug(tag = TAG, message = "getPhoto id $id")
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
        return httpClient.get("${Environment.API_SEARCH}/${Environment.API_USERS}") {
            parameter(ApiKeys.Params.QUERY, query)
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
        }.body()
    }

    override suspend fun getCollections(page: Int, perPage: Int): List<CollectionResponse> {
        appLogger.debug(tag = TAG, message = "getCollections page $page, perPage $perPage")
        return httpClient.get(Environment.API_COLLECTIONS) {
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
        }.body()
    }

    override suspend fun getCollection(id: String): CollectionResponse {
        appLogger.debug(tag = TAG, message = "getCollection id $id")
        return httpClient.get("${Environment.API_COLLECTIONS}/$id").body()
    }

    override suspend fun getTopics(page: Int, perPage: Int): List<TopicResponse> {
        appLogger.debug(tag = TAG, message = "getTopics page $page, perPage $perPage")
        return httpClient.get(Environment.API_TOPICS) {
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
        }.body()
    }

    override suspend fun getTopic(idOrSlug: String): TopicResponse {
        appLogger.debug(tag = TAG, message = "getTopic id $idOrSlug")
        return httpClient.get("${Environment.API_TOPICS}/$idOrSlug").body()
    }

    override suspend fun getTopicPhotos(
        idOrSlug: String,
        page: Int,
        perPage: Int,
        orientation: String?,
        orderBy: String?
    ): List<PhotoScheme> {
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
        return httpClient.post(urlString = Environment.OAUTH_TOKEN) {
            setBody(unsplashTokenRequest)
        }.body()
    }
}
