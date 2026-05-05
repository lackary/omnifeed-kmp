package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreviewPhotoDto(
    val id: String,
    val slug: String,
    @SerialName(ApiKeys.Common.CREATED_AT) val createdAt: String,
    @SerialName(ApiKeys.Common.UPDATED_AT) val updatedAt: String,
    @SerialName(ApiKeys.Photo.BLUR_HASH) val blurHash: String?,
    @SerialName(ApiKeys.Photo.ASSET_TYPE) val assetType: String,
    val urls: PhotoUrlsDto
)
