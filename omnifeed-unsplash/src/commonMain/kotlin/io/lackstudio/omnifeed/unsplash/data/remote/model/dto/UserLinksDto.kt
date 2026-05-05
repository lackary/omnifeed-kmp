package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserLinksDto(
    val self: String,
    val html: String,
    val photos: String,
    val likes: String,
    val portfolio: String? = null
)
