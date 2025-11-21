package io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme

import io.lackstudio.module.kmp.apiclient.unsplash.utils.constants.JsonKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoScheme(
    val id: String,
    val slug: String,
    @SerialName(JsonKeys.Photo.ALTERNATIVE_SLUGS) val alternativeSlugs: AlternativeSlugsScheme,
    @SerialName(JsonKeys.Common.CREATED_AT) val createdAt: String,
    @SerialName(JsonKeys.Common.UPDATED_AT) val updatedAt: String,
    @SerialName(JsonKeys.Photo.PROMOTED_AT) val promotedAt: String?,
    val width: Int,
    val height: Int,
    val color: String,
    @SerialName(JsonKeys.Photo.BLUR_HASH) val blurHash: String,
    val description: String? = null,
    @SerialName(JsonKeys.Photo.ALT_DESCRIPTION) val altDescription: String?,
    //    val breadcrumbs: List<Any?>,
    val urls: PhotoUrlsScheme,
    val links: PhotoLinksScheme,
    val likes: Int,
    @SerialName(JsonKeys.Photo.LIKED_BY_USER) val likedByUser: Boolean,
    val bookmarked: Boolean,
//    @SerialName(JsonKeys.Photo.CURRENT_USER_COLLECTIONS) val currentUserCollections: List<Any?>,
    val sponsorship: SponsorshipScheme? = null,
//    @SerialName(JsonKeys.Photo.TOPIC_SUBMISSIONS) val topicSubmissions: TopicSubmissions,
    @SerialName(JsonKeys.Photo.ASSET_TYPE) val assetType: String,
    val user: UserScheme
)
