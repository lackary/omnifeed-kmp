package io.lackstudio.omnifeed.unsplash.data.model.scheme

import kotlinx.serialization.Serializable

@Serializable
data class LocationScheme(
    val name: String? = null,
    val city: String? = null,
    val country: String? = null,
    val position: PositionScheme
)
