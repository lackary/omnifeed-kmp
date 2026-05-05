package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoLinksDto(
    val self: String,
    val html: String,
    val download: String,
    @SerialName(ApiKeys.Photo.DOWNLOAD_LOCATION) val downloadLocation: String
)
