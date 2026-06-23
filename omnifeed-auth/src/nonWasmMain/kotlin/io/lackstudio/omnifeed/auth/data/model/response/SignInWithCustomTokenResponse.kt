package io.lackstudio.omnifeed.auth.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class SignInWithCustomTokenResponse(
    val idToken: String? = null,
    val refreshToken: String? = null,
    val expiresIn: String? = null,
    val localId: String? = null,
    val isNewUser: Boolean = false
)
