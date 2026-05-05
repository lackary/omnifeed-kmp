package io.lackstudio.omnifeed.unsplash.data.remote.model.response

import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.AlternativeSlugsDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.BreadcrumbDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.CurrentUserCollectionDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.ExifDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.LocationDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.MetaDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PhotoLinksDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PhotoUrlsDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.RelatedCollectionDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.SponsorshipDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.TagDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.TopicSimpleDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.TopicSubmissionsDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PhotoUserDto
import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoDetailResponse(
    val id: String,
    val slug: String,
    @SerialName(ApiKeys.Photo.ALTERNATIVE_SLUGS) val alternativeSlugs: AlternativeSlugsDto,
    @SerialName(ApiKeys.Common.CREATED_AT) val createdAt: String,
    @SerialName(ApiKeys.Common.UPDATED_AT) val updatedAt: String,
    @SerialName(ApiKeys.Photo.PROMOTED_AT) val promotedAt: String? = null,
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
    @SerialName(ApiKeys.Photo.CURRENT_USER_COLLECTIONS) val currentUserCollections: List<CurrentUserCollectionDto>? = emptyList(),
    val sponsorship: SponsorshipDto? = null,
    @SerialName(ApiKeys.Photo.TOPIC_SUBMISSIONS) val topicSubmissions: TopicSubmissionsDto,
    @SerialName(ApiKeys.Photo.ASSET_TYPE) val assetType: String,
    @SerialName(ApiKeys.Photo.SHOW_ON_PROFILE) val showOnProfile: Boolean? = false,
    val user: PhotoUserDto,
    val exif: ExifDto,
    val location: LocationDto,
    val meta: MetaDto,
    @SerialName(ApiKeys.Photo.PUBLIC_DOMAIN) val publicDomain: Boolean,
    val tags: List<TagDto> = emptyList(),
    val views: Long,
    val downloads: Long,
    val topics: List<TopicSimpleDto> = emptyList(),
    @SerialName(ApiKeys.Photo.RELATED_COLLECTIONS) val relatedCollections: RelatedCollectionDto? = null
)
