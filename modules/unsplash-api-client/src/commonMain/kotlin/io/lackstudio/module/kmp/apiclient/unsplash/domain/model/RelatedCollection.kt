package io.lackstudio.module.kmp.apiclient.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RelatedCollection(
    val total: Long?,
    val type: String?,
    val results: List<Collection>?
)
