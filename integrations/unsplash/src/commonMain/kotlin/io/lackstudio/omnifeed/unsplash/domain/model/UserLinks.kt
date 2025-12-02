package io.lackstudio.omnifeed.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserLinks(
    val html: String,
    val likes: String,
    val photos: String,
    val portfolio: String,
    val self: String
)
