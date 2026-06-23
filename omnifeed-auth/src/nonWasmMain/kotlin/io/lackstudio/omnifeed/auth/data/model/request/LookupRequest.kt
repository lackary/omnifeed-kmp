package io.lackstudio.omnifeed.auth.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class LookupRequest(
    val idToken: String
)
