package io.lackstudio.omnifeed.auth.data.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAccountRequest(
    val idToken: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val password: String? = null,
    val deleteAttribute: List<String>? = null,
    val returnSecureToken: Boolean = true
)
