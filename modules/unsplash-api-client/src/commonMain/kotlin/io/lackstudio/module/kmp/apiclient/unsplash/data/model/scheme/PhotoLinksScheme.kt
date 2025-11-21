package io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme

import io.lackstudio.module.kmp.apiclient.unsplash.utils.constants.JsonKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoLinksScheme(
    val self: String,
    val html: String,
    val download: String,
    @SerialName(JsonKeys.Photo.DOWNLOAD_LOCATION) val downloadLocation: String
)
