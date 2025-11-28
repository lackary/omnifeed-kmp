package io.lackstudio.module.kmp.apiclient.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val name: String?,
    val city: String?,
    val country: String?,
    val position: Position
)

@Serializable
data class Position(
    val latitude: Double?,
    val longitude: Double?
)
