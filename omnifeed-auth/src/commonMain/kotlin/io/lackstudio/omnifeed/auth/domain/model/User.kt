package io.lackstudio.omnifeed.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isGoogleLinked: Boolean = false,
    val customLinkedServices: Map<String, Boolean> = emptyMap(),
    val idToken: String? = null // Firebase ID Token for REST-based flows
) {
    /**
     * Checks if a specific custom service is linked.
     */
    fun isCustomServiceLinked(serviceName: String): Boolean = customLinkedServices[serviceName] ?: false
}
