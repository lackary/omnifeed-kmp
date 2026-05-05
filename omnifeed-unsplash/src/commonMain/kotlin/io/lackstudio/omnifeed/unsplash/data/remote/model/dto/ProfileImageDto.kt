package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileImageDto(
    val small: String,
    val medium: String,
    val large: String
)
