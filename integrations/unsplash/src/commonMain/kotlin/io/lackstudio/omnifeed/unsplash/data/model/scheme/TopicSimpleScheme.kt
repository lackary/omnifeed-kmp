package io.lackstudio.omnifeed.unsplash.data.model.scheme

import kotlinx.serialization.Serializable

@Serializable
data class TopicSimpleScheme(
    val id: String,
    val slug: String,
    val title: String,
    val visibility: String,
)
