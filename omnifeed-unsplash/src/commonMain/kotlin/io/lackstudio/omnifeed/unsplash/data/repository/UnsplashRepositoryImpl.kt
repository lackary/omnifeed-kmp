package io.lackstudio.omnifeed.unsplash.data.repository

import co.touchlab.kermit.Logger
import io.lackstudio.omnifeed.core.common.extension.toDomain
import io.lackstudio.omnifeed.unsplash.data.local.source.LocalUnsplashPhotoDataSource
import io.lackstudio.omnifeed.unsplash.data.remote.source.RemoteUnsplashDataSource
import io.lackstudio.omnifeed.unsplash.domain.model.Collection
import io.lackstudio.omnifeed.unsplash.domain.model.Me
import io.lackstudio.omnifeed.unsplash.domain.model.OAuthCode
import io.lackstudio.omnifeed.unsplash.domain.model.OAuthToken
import io.lackstudio.omnifeed.unsplash.domain.model.Photo
import io.lackstudio.omnifeed.unsplash.domain.model.PhotoDetail
import io.lackstudio.omnifeed.unsplash.domain.model.SearchResults
import io.lackstudio.omnifeed.unsplash.domain.model.Topic
import io.lackstudio.omnifeed.unsplash.domain.model.UserProfile
import io.lackstudio.omnifeed.unsplash.domain.repository.UnsplashRepository
import io.lackstudio.omnifeed.unsplash.mapper.toCollection
import io.lackstudio.omnifeed.unsplash.mapper.toMe
import io.lackstudio.omnifeed.unsplash.mapper.toOAuthToken
import io.lackstudio.omnifeed.unsplash.mapper.toPhotoDetail
import io.lackstudio.omnifeed.unsplash.mapper.toSearchResults
import io.lackstudio.omnifeed.unsplash.mapper.toTopic
import io.lackstudio.omnifeed.unsplash.mapper.toUserProfile


internal class UnsplashRepositoryImpl(
    private val remoteUnsplashDataSource: RemoteUnsplashDataSource,
    private val localUnsplashPhotoDataSource: LocalUnsplashPhotoDataSource
) : UnsplashRepository {

    private val logger = Logger.withTag("UnsplashRepository")

    override suspend fun getMe(): Me {
        logger.d { "getMe" }
        return remoteUnsplashDataSource.getMe().toDomain(name = "getMe") { me ->
            me.toMe()
        }
    }

    override suspend fun getUserPublicProfile(username: String): UserProfile {
        logger.d { "getUserPublicProfile: $username" }
        return remoteUnsplashDataSource.getUserPublicProfile(username).toDomain(name = "getUserPublicProfile") {
            it.toUserProfile()
        }
    }

    override suspend fun getUserPhotos(
        username: String,
        page: Int,
        perPage: Int,
        orderBy: String?,
        stats: Boolean?,
        quantity: Int?,
        orientation: String?
    ): List<Photo> {
        logger.d { "getUserPhotos: $username, page: $page" }
        return remoteUnsplashDataSource.getUserPhotos(
            username = username,
            page = page,
            perPage = perPage,
            orderBy = orderBy,
            stats = stats,
            quantity = quantity,
            orientation = orientation
        ).toDomain(name = "getUserPhotos") { list ->
            list.map { it.toPhotoDetail() }
        }
    }

    override suspend fun getUserLikedPhotos(
        username: String,
        page: Int,
        perPage: Int,
        orderBy: String?,
        orientation: String?
    ): List<Photo> {
        logger.d { "getUserLikedPhotos: $username, page: $page" }
        return remoteUnsplashDataSource.getUserLikedPhotos(
            username = username,
            page = page,
            perPage = perPage,
            orderBy = orderBy,
            orientation = orientation
        ).toDomain(name = "getUserLikedPhotos") { list ->
            list.map { it.toPhotoDetail() }
        }
    }

    override suspend fun getUserCollections(
        username: String,
        page: Int,
        perPage: Int
    ): List<Collection> {
        logger.d { "getUserCollections: $username, page: $page" }
        return remoteUnsplashDataSource.getUserCollections(
            username = username,
            page = page,
            perPage = perPage
        ).toDomain(name = "getUserCollections") { list ->
            list.map { it.toCollection() }
        }
    }

    override suspend fun getPhotos(page: Int, perPage: Int): List<Photo> {
        logger.d { "getPhotos page: $page" }
        return remoteUnsplashDataSource.getPhotos(page, perPage).toDomain(name = "getPhotos") { dtoList ->
            dtoList.map { it.toPhotoDetail() }
        }
    }

    override suspend fun getPhoto(id: String): PhotoDetail {
        logger.d { "getPhoto id: $id" }
        return remoteUnsplashDataSource.getPhoto(id).toDomain(name = "getPhoto") { photo ->
//            localUnsplashPhotoDataSource.savePhoto(photo)
            photo.toPhotoDetail()
        }
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
    ): SearchResults<Photo> {
        logger.d { "searchPhotos query: $query, page: $page" }
        return remoteUnsplashDataSource.searchPhotos(
            query = query,
            page = page,
            perPage = perPage,
            orderBy = orderBy,
            collections = collections,
            contentFilter = contentFilter,
            color = color,
            orientation = orientation
        ).toDomain(name = "searchPhotos") { searchResponse ->
            searchResponse.toSearchResults { it.toPhotoDetail() }
        }
    }

    override suspend fun searchCollections(
        query: String,
        page: Int,
        perPage: Int
    ): SearchResults<Collection> {
        logger.d { "searchCollections query: $query, page: $page" }
        return remoteUnsplashDataSource.searchCollections(query, page, perPage)
            .toDomain(name = "searchCollections") { searchResponse ->
                searchResponse.toSearchResults { it.toCollection() }
            }
    }

    override suspend fun searchUsers(
        query: String,
        page: Int,
        perPage: Int
    ): SearchResults<UserProfile> {
        logger.d { "searchUsers query: $query, page: $page" }
        return remoteUnsplashDataSource.searchUsers(query, page, perPage)
            .toDomain(name = "searchUsers") { searchResponse ->
                searchResponse.toSearchResults { it.toUserProfile() }
            }
    }

    override suspend fun getCollections(page: Int, perPage: Int): List<Collection> {
        logger.d { "getCollections page: $page" }
        return remoteUnsplashDataSource.getCollections(page, perPage).toDomain(name = "getCollections") { list ->
            list.map { it.toCollection() }
        }
    }

    override suspend fun getCollection(id: String): Collection {
        logger.d { "getCollection id: $id" }
        return remoteUnsplashDataSource.getCollection(id).toDomain(name = "getCollection") { it.toCollection() }
    }

    override suspend fun getCollectionPhotos(
        id: String,
        page: Int,
        perPage: Int,
        orientation: String?
    ): List<Photo> {
        logger.d { "getCollectionPhotos id: $id, page: $page" }
        return remoteUnsplashDataSource.getCollectionPhotos(id, page, perPage, orientation)
            .toDomain(name = "getCollectionPhotos") { list ->
                list.map { it.toPhotoDetail() }
            }
    }

    override suspend fun getCollectionRelatedCollections(id: String): List<Collection> {
        logger.d { "getCollectionRelatedCollections id: $id" }
        return remoteUnsplashDataSource.getCollectionRelatedCollections(id).toDomain(name = "getCollectionRelatedCollections") { list ->
            list.map { it.toCollection() }
        }
    }

    override suspend fun getTopics(page: Int, perPage: Int): List<Topic> {
        logger.d { "getTopics page: $page" }
        return remoteUnsplashDataSource.getTopics(page, perPage).toDomain(name = "getTopics") { list ->
            list.map { it.toTopic() }
        }
    }

    override suspend fun getTopic(idOrSlug: String): Topic {
        logger.d { "getTopic id: $idOrSlug" }
        return remoteUnsplashDataSource.getTopic(idOrSlug).toDomain(name = "getTopic") { it.toTopic() }
    }

    override suspend fun getTopicPhotos(
        idOrSlug: String,
        page: Int,
        perPage: Int,
        orientation: String?,
        orderBy: String?
    ): List<Photo> {
        logger.d { "getTopicPhotos id: $idOrSlug, page: $page" }
        return remoteUnsplashDataSource.getTopicPhotos(
            idOrSlug,
            page,
            perPage,
            orientation,
            orderBy
        ).toDomain(name = "getTopicPhotos") { list ->
            list.map { it.toPhotoDetail() }
        }
    }

    override suspend fun exchangeOAuth(oAuthCode: OAuthCode): OAuthToken {
        logger.i { "exchangeOAuth starting" }
        return remoteUnsplashDataSource.exchangeOAuth(oAuthCode).toDomain(name = "exchangeOAuth") { oAuthToken ->
            oAuthToken.toOAuthToken()
        }
    }
}
