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
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.PhotoResponse
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.TokenResponse
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.TopicResponse
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.UserProfileResponse
import io.lackstudio.module.kmp.apiclient.unsplash.utils.constants.ApiKeys

class UnsplashApiServiceImpl(
    private val httpClient: HttpClient,
    private val appLogger: AppLogger
) : UnsplashApiService {
    val TAG = "UnsplashApiServiceImpl"

    override suspend fun getUserPublicProfile(username: String): UserProfileResponse {
        appLogger.debug(tag = TAG, message = "getUserPublicProfile username $username")
        return httpClient.get("${Environment.API_USERS}/$username") {
            parameter(ApiKeys.Params.USERNAME, username)
        }.body()
    }

    override suspend fun getPhotos(
        page: Int,
        perPage: Int
    ): List<PhotoResponse> {
        appLogger.debug(tag = TAG, message = "getPhotos page $page, perPage $perPage")
        return httpClient.get(Environment.API_PHOTOS) {
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
        }.body()
    }

    override suspend fun getPhoto(id: String): PhotoResponse {
        appLogger.debug(tag = TAG, message = "getPhoto id $id")
        return httpClient.get("${Environment.API_PHOTOS}/$id").body()
    }

    override suspend fun getCollections(page: Int, perPage: Int): List<CollectionResponse> {
        appLogger.debug(tag = TAG, message = "getCollections page $page, perPage $perPage")
        return httpClient.get(Environment.API_COLLECTIONS){
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
        return httpClient.get(Environment.API_TOPICS){
            parameter(ApiKeys.Params.PAGE, page)
            parameter(ApiKeys.Params.PER_PAGE, perPage)
        }.body()
    }

    override suspend fun getTopic(idOrSlug: String): TopicResponse {
        appLogger.debug(tag = TAG, message = "getTopic id $idOrSlug")
        return httpClient.get("${Environment.API_TOPICS}/$idOrSlug").body()
    }

    override suspend fun getMe(): MeProfileResponse {
        appLogger.debug(tag = TAG, message = "getMe")
        return httpClient.get(Environment.API_ME).body()
    }

    override suspend fun postOauthToken(
        unsplashTokenRequest: TokenRequest
    ): TokenResponse {
        return httpClient.post(urlString = Environment.OAUTH_TOKEN) {
            setBody(unsplashTokenRequest)
        }.body()
    }
}
