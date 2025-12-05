package io.lackstudio.omnifeed.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Badge(
    val title: String,
    val primary: Boolean,
    val slug: String,
    val link: String? = null
)
