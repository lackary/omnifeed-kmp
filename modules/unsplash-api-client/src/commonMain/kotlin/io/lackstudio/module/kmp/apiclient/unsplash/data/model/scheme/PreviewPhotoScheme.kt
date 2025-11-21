package io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme

import io.lackstudio.module.kmp.apiclient.unsplash.utils.constants.JsonKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreviewPhotoScheme(
    val id: String,
    val slug: String,
    @SerialName(JsonKeys.Common.CREATED_AT) val createdAt: String,
    @SerialName(JsonKeys.Common.UPDATED_AT) val updatedAt: String,
    @SerialName(JsonKeys.Photo.BLUR_HASH) val blurHash: String,
    @SerialName(JsonKeys.Photo.ASSET_TYPE) val assetType: String,
    val urls: PhotoUrlsScheme
)
