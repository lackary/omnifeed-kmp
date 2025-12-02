package io.lackstudio.omnifeed.unsplash.data.model.scheme

import kotlinx.serialization.Serializable

@Serializable
data class ProfileImageScheme(
    val small: String,
    val medium: String,
    val large: String
)
