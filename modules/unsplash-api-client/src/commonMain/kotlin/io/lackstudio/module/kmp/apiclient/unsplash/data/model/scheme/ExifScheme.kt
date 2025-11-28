package io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme

import io.lackstudio.module.kmp.apiclient.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExifScheme(
    val make: String? = null,
    val model: String? = null,
    val name: String? = null,
    @SerialName(ApiKeys.Exif.EXPOSURE_TIME) val exposureTime: String? = null,
    val aperture: String? = null,
    @SerialName(ApiKeys.Exif.FOCAL_LENGTH) val focalLength: String? = null,
    val iso: Int? = null
)
