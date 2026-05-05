package io.lackstudio.omnifeed.unsplash.data.remote.api

import io.lackstudio.omnifeed.unsplash.data.remote.model.request.TokenRequest
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.CollectionResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.MeProfileResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.PhotoDetailResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.SearchResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.TokenResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.TopicResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.UserProfileResponse
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PhotoDto

interface UnsplashApiService {
    suspend fun getMe(): MeProfileResponse
    suspend fun getUserPublicProfile(username: String): UserProfileResponse

    /**
     * Retrieve a user's photos. Reference by Unsplash API Documentation
     *
     * @param username The user’s username.
     * @param page Page number to retrieve. (Optional; default: 1)
     * @param perPage Number of items per page. (Optional; default: 10)
     * @param orderBy How to sort the photos. (Optional; default: latest). Valid values are: 'latest', 'oldest', 'popular'.
     * @param stats Show the stats for each user’s photo. (Optional; default: false)
//     * @param resolution The frequency of the stats. (Optional; default: "days"). Note: only "days"
     * @param quantity The amount of for each stat. (Optional; default: 30).
     * @param orientation Filter by photo orientation. (Optional). Valid values are: 'landscape', 'portrait', 'squarish'.
     * @return A list of [PhotoDto] belonging to the specified user.
     */
    suspend fun getUserPhotos(
        username: String,
        page: Int,
        perPage: Int,
        orderBy: String? = null,
        stats: Boolean? = null,
//        resolution: String? = null,
        quantity: Int? = null,
        orientation: String? = null
    ): List<PhotoDto>

    /**
     * Retrieve a list of photos liked by a user. Reference by Unsplash API Documentation
     *
     ** @param username The user’s username.
     * @param page Page number to retrieve. (Optional; default: 1)
     * @param perPage Number of items per page. (Optional; default: 10)
     * @param orderBy How to sort the photos. (Optional; default: latest). Valid values are: 'latest', 'oldest', 'popular'.
     * @param orientation Filter by photo orientation. (Optional). Valid values are: 'landscape', 'portrait', 'squarish'.
     * @return A list of [PhotoDto] containing the photos liked by the specified user.
     */
    suspend fun getUserLikedPhotos(
        username: String,
        page: Int,
        perPage: Int,
        orderBy: String? = null,
        orientation: String? = null
    ): List<PhotoDto>

    suspend fun getUserCollections(
        username: String,
        page: Int,
        perPage: Int,
    ): List<CollectionResponse>

    suspend fun getPhotos(page: Int, perPage: Int): List<PhotoDto>
    suspend fun getPhoto(id: String): PhotoDetailResponse

//    suspend fun getRandomPhotos(): List<PhotoResponse>
//    suspend fun getRandomPhoto(): PhotoResponse

    /**
     * Search for photos based on a query term. Reference by Unsplash API Documentation
     *
     * @param query Search terms.
     * @param page Page number to retrieve. (Optional; default: 1)
     * @param perPage Number of items per page. (Optional; default: 10)
     * @param orderBy How to sort the photos. (Optional; default: relevant). Valid values are: 'latest', 'relevant'.
     * @param collections Collection IDs to narrow search. If multiple, use a comma-separated string.
     * @param contentFilter Limit results by content safety. (Optional; default: low). Valid values are: 'low', 'high'.
     * @param color Filter results by color. Valid values are: 'black_and_white', 'black', 'white', 'yellow', 'orange', 'red', 'purple', 'magenta', 'green', 'teal', and 'blue'.
     * @param orientation Filter by photo orientation. Valid values are: 'landscape', 'portrait', 'squarish'.
     * @return A [SearchResponse] containing a list of [PhotoDto]/[CollectionResponse]/[UserProfileResponse] matching the criteria.
     */
    suspend fun searchPhotos(
        query: String,
        page: Int,
        perPage: Int,
        orderBy: String? = null,
        collections: String? = null,
        contentFilter: String? = null,
        color: String? = null,
        orientation: String? = null,
    ): SearchResponse<PhotoDto>
    suspend fun searchCollections(query: String, page: Int, perPage: Int): SearchResponse<CollectionResponse>
    suspend fun searchUsers(query: String, page: Int, perPage: Int): SearchResponse<UserProfileResponse>
    suspend fun getCollections(page: Int, perPage: Int): List<CollectionResponse>
    suspend fun getCollection(id: String): CollectionResponse

    /**
     * Retrieve a collection's photos. Reference by Unsplash API Documentation
     *
     * @param id The collection’s ID.
     * @param page Page number to retrieve. (Optional; default: 1)
     * @param perPage Number of items per page. (Optional; default: 10)
     * @param orientation Filter by photo orientation. (Optional). Valid values are: 'landscape', 'portrait', 'squarish'.
     * @return A list of [PhotoDto] containing photos from the specified collection.
     */
    suspend fun getCollectionPhotos(
        id: String,
        page: Int,
        perPage: Int,
        orientation: String? = null
    ): List<PhotoDto>
    
    suspend fun getCollectionRelatedCollections(
        id: String
    ): List<CollectionResponse>

    suspend fun getTopics(page: Int, perPage: Int): List<TopicResponse>
    suspend fun getTopic(idOrSlug: String): TopicResponse

    /**
     * Retrieve a topic’s photos.
     *
     * @param idOrSlug The topic’s ID or slug.
     * @param page Page number to retrieve. (Optional; default: 1)
     * @param perPage Number of items per page. (Optional; default: 10)
     * @param orientation Filter by photo orientation. (Optional). Valid values are: 'landscape', 'portrait', 'squarish'.
     * @param orderBy How to sort the photos. (Optional; default: latest). Valid values are: 'latest', 'oldest', 'popular'.
     * @return A list of [TopicResponse] containing photos from the specified topic.
     */
    suspend fun getTopicPhotos(
        idOrSlug: String,
        page: Int,
        perPage: Int,
        orientation: String? = null,
        orderBy: String? = null
    ): List<PhotoDto>

    /**
     *  Get an access token by making a POST request according to the Unsplash API documentation.
     *  https://unsplash.com/documentation/user-authentication-workflow#step-2-the-user-approves-the-application
     */
    suspend fun postOauthToken(
        unsplashTokenRequest: TokenRequest
    ): TokenResponse
}
