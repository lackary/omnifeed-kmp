package io.lackstudio.omnifeed.unsplash.data.model.scheme

import kotlinx.serialization.Serializable

@Serializable
data class TagsScheme(
    val custom: List<TagScheme>,
    val aggregated: List<TagScheme>
)
