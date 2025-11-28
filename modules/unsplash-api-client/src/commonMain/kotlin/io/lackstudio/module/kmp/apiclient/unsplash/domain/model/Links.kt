package io.lackstudio.module.kmp.apiclient.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Links(
    val self: String,
    val html: String,
    val photos: String,
    val related: String? = null
)
