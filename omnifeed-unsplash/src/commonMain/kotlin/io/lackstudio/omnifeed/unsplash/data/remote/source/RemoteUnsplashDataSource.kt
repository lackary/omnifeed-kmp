package io.lackstudio.omnifeed.unsplash.data.remote.source

import io.lackstudio.omnifeed.unsplash.data.remote.model.response.CollectionResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.MeProfileResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.PhotoDetailResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.SearchResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.TokenResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.TopicResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.UserProfileResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PhotoDto
import io.lackstudio.omnifeed.unsplash.domain.model.OAuthCode

interface RemoteUnsplashDataSource {
    suspend fun getMe(): Result<MeProfileResponse>
    suspend fun getUserPublicProfile(username: String): Result<UserProfileResponse>

    suspend fun getUserPhotos(
        username: String,
        page: Int,
        perPage: Int,
        orderBy: String? = null,
        stats: Boolean? = null,
        quantity: Int? = null,
        orientation: String? = null
    ): Result<List<PhotoDto>>

    suspend fun getUserLikedPhotos(
        username: String,
        page: Int,
        perPage: Int,
        orderBy: String? = null,
        orientation: String? = null
    ): Result<List<PhotoDto>>

    suspend fun getUserCollections(
        username: String,
        page: Int,
        perPage: Int,
    ): Result<List<CollectionResponse>>

    suspend fun getPhotos(page: Int, perPage: Int): Result<List<PhotoDto>>
    suspend fun getPhoto(id: String): Result<PhotoDetailResponse>

    suspend fun searchPhotos(
        query: String,
        page: Int,
        perPage: Int,
        orderBy: String? = null,
        collections: String? = null,
        contentFilter: String? = null,
        color: String? = null,
        orientation: String? = null,
    ): Result<SearchResponse<PhotoDto>>

    suspend fun searchCollections(query: String, page: Int, perPage: Int): Result<SearchResponse<CollectionResponse>>
    suspend fun searchUsers(query: String, page: Int, perPage: Int): Result<SearchResponse<UserProfileResponse>>

    suspend fun getCollections(page: Int, perPage: Int): Result<List<CollectionResponse>>
    suspend fun getCollection(id: String): Result<CollectionResponse>

    suspend fun getCollectionPhotos(
        id: String,
        page: Int,
        perPage: Int,
        orientation: String? = null
    ): Result<List<PhotoDto>>

    suspend fun getCollectionRelatedCollections(
        id: String
    ): Result<List<CollectionResponse>>

    suspend fun getTopics(page: Int, perPage: Int): Result<List<TopicResponse>>
    suspend fun getTopic(idOrSlug: String): Result<TopicResponse>

    suspend fun getTopicPhotos(
        idOrSlug: String,
        page: Int,
        perPage: Int,
        orientation: String? = null,
        orderBy: String? = null
    ): Result<List<PhotoDto>>

    suspend fun exchangeOAuth(oAuthCode: OAuthCode): Result<TokenResponse>
}
