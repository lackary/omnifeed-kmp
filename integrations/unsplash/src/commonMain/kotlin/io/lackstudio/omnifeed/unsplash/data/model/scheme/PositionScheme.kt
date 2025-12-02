package io.lackstudio.omnifeed.unsplash.data.model.scheme

import kotlinx.serialization.Serializable

@Serializable
data class PositionScheme(
    val latitude: Double? = null,
    val longitude: Double? = null
)
