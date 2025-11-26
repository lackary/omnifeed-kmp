package io.lackstudio.module.kmp.apiclient.unsplash.data.model.response

import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.LinksScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.MetaScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.PhotoScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.PreviewPhotoScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.UserScheme
import io.lackstudio.module.kmp.apiclient.unsplash.utils.constants.ApiKeys
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
    val links: LinksScheme,
    val user: UserScheme,
    @SerialName(ApiKeys.Common.COVER_PHOTO) val coverPhoto: PhotoScheme? = null,
    @SerialName(ApiKeys.Common.PREVIEW_PHOTOS) val previewPhotos: List<PreviewPhotoScheme>? = emptyList(),
    val meta: MetaScheme? = null,
    @SerialName(ApiKeys.Common.MEDIA_TYPES) val mediaTypes: List<String>? = emptyList()
)
