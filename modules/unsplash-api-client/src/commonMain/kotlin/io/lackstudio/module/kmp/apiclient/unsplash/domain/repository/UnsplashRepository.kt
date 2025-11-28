package io.lackstudio.module.kmp.apiclient.unsplash.domain.repository

import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Collection
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Me
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.OAuthCode
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.OAuthToken
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Photo
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.PhotoDetail
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.SearchResults
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Topic
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.UserProfile

interface UnsplashRepository {
    suspend fun getMe(): Me
    
    suspend fun getUserPublicProfile(username: String): UserProfile
    
    suspend fun getUserPhotos(
        username: String,
        page: Int,
        perPage: Int,
        orderBy: String? = null,
        stats: Boolean? = null,
        quantity: Int? = null,
        orientation: String? = null
    ): List<Photo>
    
    suspend fun getUserLikedPhotos(
        username: String,
        page: Int,
        perPage: Int,
        orderBy: String? = null,
        orientation: String? = null
    ): List<Photo>
    
    suspend fun getUserCollections(
        username: String,
        page: Int,
        perPage: Int
    ): List<Collection>

    suspend fun getPhotos(page: Int, perPage: Int): List<Photo>
    
    suspend fun getPhoto(id: String): PhotoDetail
    
    suspend fun searchPhotos(
        query: String,
        page: Int,
        perPage: Int,
        orderBy: String? = null,
        collections: String? = null,
        contentFilter: String? = null,
        color: String? = null,
        orientation: String? = null
    ): SearchResults<Photo>
    
    suspend fun searchCollections(
        query: String,
        page: Int,
        perPage: Int
    ): SearchResults<Collection>
    
    suspend fun searchUsers(
        query: String,
        page: Int,
        perPage: Int
    ): SearchResults<UserProfile>
    
    suspend fun getCollections(page: Int, perPage: Int): List<Collection>
    
    suspend fun getCollection(id: String): Collection
    
    suspend fun getCollectionPhotos(
        id: String,
        page: Int,
        perPage: Int,
        orientation: String? = null
    ): List<Photo>
    
    suspend fun getCollectionRelatedCollections(id: String): List<Collection>
    
    suspend fun getTopics(page: Int, perPage: Int): List<Topic>
    
    suspend fun getTopic(idOrSlug: String): Topic
    
    suspend fun getTopicPhotos(
        idOrSlug: String,
        page: Int,
        perPage: Int,
        orientation: String? = null,
        orderBy: String? = null
    ): List<Photo>
    
    suspend fun exchangeOAuth(oAuthCode: OAuthCode): OAuthToken
}
