package io.lackstudio.omnifeed.unsplash.data.remote

import io.lackstudio.omnifeed.unsplash.data.model.response.CollectionResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.MeProfileResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.PhotoDetailResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.SearchResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.TokenResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.TopicResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.UserProfileResponse
import io.lackstudio.omnifeed.unsplash.data.model.scheme.PhotoScheme
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
    ): Result<List<PhotoScheme>>

    suspend fun getUserLikedPhotos(
        username: String,
        page: Int,
        perPage: Int,
        orderBy: String? = null,
        orientation: String? = null
    ): Result<List<PhotoScheme>>

    suspend fun getUserCollections(
        username: String,
        page: Int,
        perPage: Int,
    ): Result<List<CollectionResponse>>

    suspend fun getPhotos(page: Int, perPage: Int): Result<List<PhotoScheme>>
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
    ): Result<SearchResponse<PhotoScheme>>

    suspend fun searchCollections(query: String, page: Int, perPage: Int): Result<SearchResponse<CollectionResponse>>
    suspend fun searchUsers(query: String, page: Int, perPage: Int): Result<SearchResponse<UserProfileResponse>>

    suspend fun getCollections(page: Int, perPage: Int): Result<List<CollectionResponse>>
    suspend fun getCollection(id: String): Result<CollectionResponse>

    suspend fun getCollectionPhotos(
        id: String,
        page: Int,
        perPage: Int,
        orientation: String? = null
    ): Result<List<PhotoScheme>>

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
    ): Result<List<PhotoScheme>>

    suspend fun exchangeOAuth(oAuthCode: OAuthCode): Result<TokenResponse>
}
