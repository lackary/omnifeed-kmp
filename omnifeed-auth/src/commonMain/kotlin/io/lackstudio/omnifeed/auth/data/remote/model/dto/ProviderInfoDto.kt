package io.lackstudio.omnifeed.auth.data.remote.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProviderInfoDto(
    val providerId: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val email: String? = null
)
