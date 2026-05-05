package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class LinksDto(
    val self: String,
    val html: String,
    val photos: String,
    val related: String? = null
)
