package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class PositionDto(
    val latitude: Double? = null,
    val longitude: Double? = null
)
