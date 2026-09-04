package io.lackstudio.omnifeed.auth.data.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthErrorResponse(
    val error: AuthErrorDetail? = null
)

@Serializable
data class AuthErrorDetail(
    val code: Int? = null,
    val message: String? = null,
    val errors: List<AuthErrorItem>? = null
)

@Serializable
data class AuthErrorItem(
    val message: String? = null,
    val domain: String? = null,
    val reason: String? = null
)
