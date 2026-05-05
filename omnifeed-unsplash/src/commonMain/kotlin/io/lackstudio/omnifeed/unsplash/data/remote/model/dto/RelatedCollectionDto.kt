package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import io.lackstudio.omnifeed.unsplash.data.remote.model.response.CollectionResponse
import kotlinx.serialization.Serializable

@Serializable
data class RelatedCollectionDto (
    val total: Long? = 0,
    val type: String? = null,
    val results: List<CollectionResponse>? = emptyList()
)
