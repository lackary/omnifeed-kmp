package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoDto(
    val id: String,
    val slug: String,
    @SerialName(ApiKeys.Photo.ALTERNATIVE_SLUGS) val alternativeSlugs: AlternativeSlugsDto,
    @SerialName(ApiKeys.Common.CREATED_AT) val createdAt: String,
    @SerialName(ApiKeys.Common.UPDATED_AT) val updatedAt: String,
    @SerialName(ApiKeys.Photo.PROMOTED_AT) val promotedAt: String?,
    val width: Int,
    val height: Int,
    val color: String,
    @SerialName(ApiKeys.Photo.BLUR_HASH) val blurHash: String? = null,
    val description: String? = null,
    @SerialName(ApiKeys.Photo.ALT_DESCRIPTION) val altDescription: String? = null,
    val breadcrumbs: List<BreadcrumbDto>? = emptyList(),
    val urls: PhotoUrlsDto,
    val links: PhotoLinksDto,
    val likes: Int,
    @SerialName(ApiKeys.Photo.LIKED_BY_USER) val likedByUser: Boolean,
    val bookmarked: Boolean,
    @SerialName(ApiKeys.Photo.CURRENT_USER_COLLECTIONS) val currentUserCollections: List<CurrentUserCollectionDto> = emptyList(),
    val sponsorship: SponsorshipDto? = null,
    @SerialName(ApiKeys.Photo.TOPIC_SUBMISSIONS) val topicSubmissions: TopicSubmissionsDto,
    @SerialName(ApiKeys.Photo.ASSET_TYPE) val assetType: String,
    val user: PhotoUserDto
)
