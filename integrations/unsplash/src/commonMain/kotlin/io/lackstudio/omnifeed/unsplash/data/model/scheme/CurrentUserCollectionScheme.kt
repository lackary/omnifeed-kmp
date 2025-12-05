package io.lackstudio.omnifeed.unsplash.data.model.scheme

import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentUserCollectionScheme(
    val id: String,
    val title: String,
    val description: String? = null,
    @SerialName(ApiKeys.Common.PUBLISHED_AT) val publishedAt: String,
    @SerialName(ApiKeys.Collection.LAST_COLLECTED_AT) val lastCollectedAt: String,
    @SerialName(ApiKeys.Common.UPDATED_AT) val updatedAt: String,
    val featured: Boolean,
    @SerialName(ApiKeys.Statistics.TOTAL_PHOTOS) val totalPhotos: Int,
    val private: Boolean,
    @SerialName(ApiKeys.Collection.SHARE_KEY) val shareKey: String,
    val links: PhotoLinksScheme
)
