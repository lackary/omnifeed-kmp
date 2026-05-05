package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class TopicSimpleDto(
    val id: String,
    val slug: String,
    val title: String,
    val visibility: String,
)
