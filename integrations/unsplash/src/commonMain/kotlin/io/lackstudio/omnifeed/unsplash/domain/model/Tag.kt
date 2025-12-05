package io.lackstudio.omnifeed.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val type: String,
    val title: String
)
