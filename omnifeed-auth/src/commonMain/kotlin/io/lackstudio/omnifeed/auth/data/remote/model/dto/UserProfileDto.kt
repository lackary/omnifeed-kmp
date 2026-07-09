package io.lackstudio.omnifeed.auth.data.remote.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val authProviders: Map<String, Boolean>? = null,
    val linkedServices: Map<String, Boolean>? = null
)
