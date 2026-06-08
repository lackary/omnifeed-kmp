package io.lackstudio.omnifeed.auth.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class DeleteAccountRequest(
    val idToken: String
)
