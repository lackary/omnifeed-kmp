package io.lackstudio.omnifeed.unsplash.data.model.scheme

import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoUrlsScheme(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String,
    @SerialName(ApiKeys.Urls.SMALL_S3) val smallS3: String
)
