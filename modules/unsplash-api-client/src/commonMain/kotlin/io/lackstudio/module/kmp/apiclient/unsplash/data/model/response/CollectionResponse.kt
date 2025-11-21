package io.lackstudio.module.kmp.apiclient.unsplash.data.model.response

import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.LinksScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.MetaScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.PhotoScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.PreviewPhotoScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.UserScheme
import io.lackstudio.module.kmp.apiclient.unsplash.utils.constants.JsonKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionResponse(
    val id: String,
    val title: String,
    val description: String? = null,
    @SerialName(JsonKeys.Collection.PUBLISHED_AT) val publishedAt: String,
    @SerialName(JsonKeys.Collection.LAST_COLLECTED_AT) val lastCollectedAt: String,
    @SerialName(JsonKeys.Common.UPDATED_AT) val updatedAt: String,
    val featured: Boolean,
    @SerialName(JsonKeys.Statistics.TOTAL_PHOTOS) val totalPhotos: Int,
    val `private`: Boolean,
    @SerialName(JsonKeys.Collection.SHARE_KEY) val shareKey: String,
    val links: LinksScheme,
    val user: UserScheme,
    @SerialName(JsonKeys.Collection.COVER_PHOTO) val coverPhoto: PhotoScheme,
    @SerialName(JsonKeys.Collection.PREVIEW_PHOTOS) val previewPhotos: List<PreviewPhotoScheme>,
    val meta: MetaScheme,
    @SerialName(JsonKeys.Collection.MEDIA_TYPES) val mediaTypes: List<String>
)
