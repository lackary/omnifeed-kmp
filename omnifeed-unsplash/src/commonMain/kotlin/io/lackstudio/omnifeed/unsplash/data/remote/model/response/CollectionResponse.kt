package io.lackstudio.omnifeed.unsplash.data.remote.model.response

import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.LinksDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.MetaDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PhotoDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PreviewPhotoDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PhotoUserDto
import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionResponse(
    val id: String,
    val title: String,
    val description: String? = null,
    @SerialName(ApiKeys.Common.PUBLISHED_AT) val publishedAt: String,
    @SerialName(ApiKeys.Collection.LAST_COLLECTED_AT) val lastCollectedAt: String,
    @SerialName(ApiKeys.Common.UPDATED_AT) val updatedAt: String? = null,
    val featured: Boolean,
    @SerialName(ApiKeys.Statistics.TOTAL_PHOTOS) val totalPhotos: Int,
    val private: Boolean,
    @SerialName(ApiKeys.Collection.SHARE_KEY) val shareKey: String,
    val links: LinksDto,
    val user: PhotoUserDto,
    @SerialName(ApiKeys.Common.COVER_PHOTO) val coverPhoto: PhotoDto? = null,
    @SerialName(ApiKeys.Common.PREVIEW_PHOTOS) val previewPhotos: List<PreviewPhotoDto>? = emptyList(),
    val meta: MetaDto? = null,
    @SerialName(ApiKeys.Common.MEDIA_TYPES) val mediaTypes: List<String>? = emptyList()
)
