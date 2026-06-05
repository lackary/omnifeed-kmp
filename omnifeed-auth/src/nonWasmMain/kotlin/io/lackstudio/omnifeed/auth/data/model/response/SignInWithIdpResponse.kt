package io.lackstudio.omnifeed.auth.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class SignInWithIdpResponse(
    val idToken: String? = null,
    val email: String? = null,
    val localId: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null
)
