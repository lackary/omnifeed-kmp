package io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme

import kotlinx.serialization.Serializable

@Serializable
data class PositionScheme(
    val latitude: Double? = null,
    val longitude: Double? = null
)
