package io.lackstudio.omnifeed.auth.data.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class SignInWithCustomTokenResponse(
    val idToken: String? = null,
    val refreshToken: String? = null,
    val expiresIn: String? = null
)
