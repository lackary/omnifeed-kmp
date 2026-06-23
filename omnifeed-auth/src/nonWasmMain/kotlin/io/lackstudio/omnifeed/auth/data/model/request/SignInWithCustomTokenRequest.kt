package io.lackstudio.omnifeed.auth.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class SignInWithCustomTokenRequest(
    val token: String,
    val returnSecureToken: Boolean = true
)
