package io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TexturesPatternsScheme(
    val status: String,
    @SerialName("approved_on") val approvedOn: String
)
