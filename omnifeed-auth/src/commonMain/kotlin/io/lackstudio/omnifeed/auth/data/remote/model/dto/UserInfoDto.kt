package io.lackstudio.omnifeed.auth.data.remote.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    val localId: String,
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val providerUserInfo: List<ProviderInfoDto> = emptyList()
)
