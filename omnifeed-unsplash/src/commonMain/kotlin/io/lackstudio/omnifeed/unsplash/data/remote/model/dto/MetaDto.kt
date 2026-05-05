package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class MetaDto(
    val title: String? = null,
    val description: String? = null,
    val index: Boolean
)
