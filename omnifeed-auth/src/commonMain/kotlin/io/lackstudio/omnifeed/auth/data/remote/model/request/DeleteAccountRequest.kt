package io.lackstudio.omnifeed.auth.data.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class DeleteAccountRequest(
    val idToken: String
)
