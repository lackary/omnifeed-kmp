package io.lackstudio.omnifeed.auth.data.remote.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val isGoogleLinked: Boolean? = null,
    // Fields used for reading
    val customFields: Map<String, Boolean> = emptyMap()
)
