package io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme

import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.CollectionResponse
import kotlinx.serialization.Serializable

@Serializable
data class RelatedCollectionScheme (
    val total: Long? = 0,
    val type: String? = null,
    val results: List<CollectionResponse>? = emptyList()
)
