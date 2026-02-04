package io.lackstudio.omnifeed.unsplash.data.model.scheme

import kotlinx.serialization.Serializable

@Serializable
data class LinksScheme(
    val self: String,
    val html: String,
    val photos: String,
    val related: String? = null
)
