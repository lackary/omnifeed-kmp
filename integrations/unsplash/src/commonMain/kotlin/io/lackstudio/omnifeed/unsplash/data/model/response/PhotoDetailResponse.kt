package io.lackstudio.omnifeed.unsplash.data.model.response

import io.lackstudio.omnifeed.unsplash.data.model.scheme.AlternativeSlugsScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.BreadcrumbScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.CurrentUserCollectionScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.ExifScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.LocationScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.MetaScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.PhotoLinksScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.PhotoUrlsScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.RelatedCollectionScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.SponsorshipScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.TagScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.TopicSimpleScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.TopicSubmissionsScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.UserScheme
import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoDetailResponse(
    val id: String,
    val slug: String,
    @SerialName(ApiKeys.Photo.ALTERNATIVE_SLUGS) val alternativeSlugs: AlternativeSlugsScheme,
    @SerialName(ApiKeys.Common.CREATED_AT) val createdAt: String,
    @SerialName(ApiKeys.Common.UPDATED_AT) val updatedAt: String,
    @SerialName(ApiKeys.Photo.PROMOTED_AT) val promotedAt: String? = null,
    val width: Int,
    val height: Int,
    val color: String,
    @SerialName(ApiKeys.Photo.BLUR_HASH) val blurHash: String,
    val description: String? = null,
    @SerialName(ApiKeys.Photo.ALT_DESCRIPTION) val altDescription: String? = null,
    val breadcrumbs: List<BreadcrumbScheme>? = emptyList(),
    val urls: PhotoUrlsScheme,
    val links: PhotoLinksScheme,
    val likes: Int,
    @SerialName(ApiKeys.Photo.LIKED_BY_USER) val likedByUser: Boolean,
    val bookmarked: Boolean,
    @SerialName(ApiKeys.Photo.CURRENT_USER_COLLECTIONS) val currentUserCollections: List<CurrentUserCollectionScheme>? = emptyList(),
    val sponsorship: SponsorshipScheme? = null,
    @SerialName(ApiKeys.Photo.TOPIC_SUBMISSIONS) val topicSubmissions: TopicSubmissionsScheme,
    @SerialName(ApiKeys.Photo.ASSET_TYPE) val assetType: String,
    @SerialName(ApiKeys.Photo.SHOW_ON_PROFILE) val showOnProfile: Boolean? = false,
    val user: UserScheme,
    val exif: ExifScheme,
    val location: LocationScheme,
    val meta: MetaScheme,
    @SerialName(ApiKeys.Photo.PUBLIC_DOMAIN) val publicDomain: Boolean,
    val tags: List<TagScheme> = emptyList(),
    val views: Long,
    val downloads: Long,
    val topics: List<TopicSimpleScheme> = emptyList(),
    @SerialName(ApiKeys.Photo.RELATED_COLLECTIONS) val relatedCollections: RelatedCollectionScheme? = null
)
