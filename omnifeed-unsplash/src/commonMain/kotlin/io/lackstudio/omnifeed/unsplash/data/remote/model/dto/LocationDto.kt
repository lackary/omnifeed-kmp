package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    val name: String? = null,
    val city: String? = null,
    val country: String? = null,
    val position: PositionDto
)
