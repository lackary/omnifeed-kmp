package io.lackstudio.omnifeed.unsplash.data.model.scheme

import kotlinx.serialization.Serializable

@Serializable
data class MetaScheme(
    val title: String? = null,
    val description: String? = null,
    val index: Boolean
)
