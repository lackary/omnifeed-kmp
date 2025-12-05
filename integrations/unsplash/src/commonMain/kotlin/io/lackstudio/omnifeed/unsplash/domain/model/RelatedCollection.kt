package io.lackstudio.omnifeed.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RelatedCollection(
    val total: Long?,
    val type: String?,
    val results: List<Collection>?
)
