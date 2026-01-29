package io.lackstudio.omnifeed.unsplash.data.repository

import io.lackstudio.omnifeed.core.common.extension.toDomain
import io.lackstudio.omnifeed.unsplash.data.local.LocalUnsplashPhotoDataSource
import io.lackstudio.omnifeed.unsplash.data.remote.RemoteUnsplashDataSource
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

    override suspend fun getMe(): Me {
        return remoteUnsplashDataSource.getMe().toDomain { me ->
            me.toMe()
        }
    }

    override suspend fun getUserPublicProfile(username: String): UserProfile {
        return remoteUnsplashDataSource.getUserPublicProfile(username).toDomain {
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
        return remoteUnsplashDataSource.getUserPhotos(
            username = username,
            page = page,
            perPage = perPage,
            orderBy = orderBy,
            stats = stats,
            quantity = quantity,
            orientation = orientation
        ).toDomain { list ->
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
        return remoteUnsplashDataSource.getUserLikedPhotos(
            username = username,
            page = page,
            perPage = perPage,
            orderBy = orderBy,
            orientation = orientation
        ).toDomain { list ->
            list.map { it.toPhotoDetail() }
        }
    }

    override suspend fun getUserCollections(
        username: String,
        page: Int,
        perPage: Int
    ): List<Collection> {
        return remoteUnsplashDataSource.getUserCollections(
            username = username,
            page = page,
            perPage = perPage
        ).toDomain { list ->
            list.map { it.toCollection() }
        }
    }

    override suspend fun getPhotos(page: Int, perPage: Int): List<Photo> {
        return remoteUnsplashDataSource.getPhotos(page, perPage).toDomain { dtoList ->
            dtoList.map { it.toPhotoDetail() }
        }
    }

    override suspend fun getPhoto(id: String): PhotoDetail {
        return remoteUnsplashDataSource.getPhoto(id).toDomain { photo ->
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
        return remoteUnsplashDataSource.searchPhotos(
            query = query,
            page = page,
            perPage = perPage,
            orderBy = orderBy,
            collections = collections,
            contentFilter = contentFilter,
            color = color,
            orientation = orientation
        ).toDomain { searchResponse ->
            searchResponse.toSearchResults { it.toPhotoDetail() }
        }
    }

    override suspend fun searchCollections(
        query: String,
        page: Int,
        perPage: Int
    ): SearchResults<Collection> {
        return remoteUnsplashDataSource.searchCollections(query, page, perPage)
            .toDomain { searchResponse ->
                searchResponse.toSearchResults { it.toCollection() }
            }
    }

    override suspend fun searchUsers(
        query: String,
        page: Int,
        perPage: Int
    ): SearchResults<UserProfile> {
        return remoteUnsplashDataSource.searchUsers(query, page, perPage)
            .toDomain { searchResponse ->
                searchResponse.toSearchResults { it.toUserProfile() }
            }
    }

    override suspend fun getCollections(page: Int, perPage: Int): List<Collection> {
        return remoteUnsplashDataSource.getCollections(page, perPage).toDomain { list ->
            list.map { it.toCollection() }
        }
    }

    override suspend fun getCollection(id: String): Collection {
        return remoteUnsplashDataSource.getCollection(id).toDomain { it.toCollection() }
    }

    override suspend fun getCollectionPhotos(
        id: String,
        page: Int,
        perPage: Int,
        orientation: String?
    ): List<Photo> {
        return remoteUnsplashDataSource.getCollectionPhotos(id, page, perPage, orientation)
            .toDomain { list ->
                list.map { it.toPhotoDetail() }
            }
    }

    override suspend fun getCollectionRelatedCollections(id: String): List<Collection> {
        return remoteUnsplashDataSource.getCollectionRelatedCollections(id).toDomain { list ->
            list.map { it.toCollection() }
        }
    }

    override suspend fun getTopics(page: Int, perPage: Int): List<Topic> {
        return remoteUnsplashDataSource.getTopics(page, perPage).toDomain { list ->
            list.map { it.toTopic() }
        }
    }

    override suspend fun getTopic(idOrSlug: String): Topic {
        return remoteUnsplashDataSource.getTopic(idOrSlug).toDomain { it.toTopic() }
    }

    override suspend fun getTopicPhotos(
        idOrSlug: String,
        page: Int,
        perPage: Int,
        orientation: String?,
        orderBy: String?
    ): List<Photo> {
        return remoteUnsplashDataSource.getTopicPhotos(
            idOrSlug,
            page,
            perPage,
            orientation,
            orderBy
        ).toDomain { list ->
            list.map { it.toPhotoDetail() }
        }
    }

    override suspend fun exchangeOAuth(oAuthCode: OAuthCode): OAuthToken {
        return remoteUnsplashDataSource.exchangeOAuth(oAuthCode).toDomain { oAuthToken ->
            oAuthToken.toOAuthToken()
        }
    }
}
