package io.lackstudio.omnifeed.auth.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class LookupResponse(
    val users: List<UserInfoResponse> = emptyList()
)

@Serializable
data class UserInfoResponse(
    val localId: String,
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val providerUserInfo: List<ProviderInfoResponse> = emptyList()
)

@Serializable
data class ProviderInfoResponse(
    val providerId: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val email: String? = null
)
