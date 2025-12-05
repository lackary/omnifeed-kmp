package io.lackstudio.omnifeed.unsplash.data.model.scheme

import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoScheme(
    val id: String,
    val slug: String,
    @SerialName(ApiKeys.Photo.ALTERNATIVE_SLUGS) val alternativeSlugs: AlternativeSlugsScheme,
    @SerialName(ApiKeys.Common.CREATED_AT) val createdAt: String,
    @SerialName(ApiKeys.Common.UPDATED_AT) val updatedAt: String,
    @SerialName(ApiKeys.Photo.PROMOTED_AT) val promotedAt: String?,
    val width: Int,
    val height: Int,
    val color: String,
    @SerialName(ApiKeys.Photo.BLUR_HASH) val blurHash: String,
    val description: String? = null,
    @SerialName(ApiKeys.Photo.ALT_DESCRIPTION) val altDescription: String?,
    val breadcrumbs: List<BreadcrumbScheme>? = emptyList(),
    val urls: PhotoUrlsScheme,
    val links: PhotoLinksScheme,
    val likes: Int,
    @SerialName(ApiKeys.Photo.LIKED_BY_USER) val likedByUser: Boolean,
    val bookmarked: Boolean,
    @SerialName(ApiKeys.Photo.CURRENT_USER_COLLECTIONS) val currentUserCollections: List<CurrentUserCollectionScheme> = emptyList(),
    val sponsorship: SponsorshipScheme? = null,
    @SerialName(ApiKeys.Photo.TOPIC_SUBMISSIONS) val topicSubmissions: TopicSubmissionsScheme,
    @SerialName(ApiKeys.Photo.ASSET_TYPE) val assetType: String,
    val user: UserScheme
)
