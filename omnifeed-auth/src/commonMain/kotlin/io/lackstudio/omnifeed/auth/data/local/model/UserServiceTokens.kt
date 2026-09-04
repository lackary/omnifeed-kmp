package io.lackstudio.omnifeed.auth.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class UserServiceTokens(
    val userId: String,
    val tokens: Map<String, String> = emptyMap()
)
