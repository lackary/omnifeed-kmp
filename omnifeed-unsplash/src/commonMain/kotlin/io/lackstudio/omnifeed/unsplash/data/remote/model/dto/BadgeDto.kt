package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class BadgeDto(
    val title: String,
    val primary: Boolean,
    val slug: String,
    val link: String? = null
)
