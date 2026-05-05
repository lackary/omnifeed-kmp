package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoUrlsDto(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String,
    @SerialName(ApiKeys.Urls.SMALL_S3) val smallS3: String
)
