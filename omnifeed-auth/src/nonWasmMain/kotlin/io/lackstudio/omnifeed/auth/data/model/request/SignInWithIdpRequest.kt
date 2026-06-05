package io.lackstudio.omnifeed.auth.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class SignInWithIdpRequest(
    val postBody: String,
    val requestUri: String = "http://localhost",
    val returnIdpCredential: Boolean = true,
    val returnSecureToken: Boolean = true
)
