package io.lackstudio.omnifeed.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String?,
    val username: String?,
    val photoUrl: String?,
    val authProviders: Map<String, Boolean> = emptyMap(),
    val linkedServices: Map<String, Boolean> = emptyMap(),
    val lastSignInProvider: String? = null, // Tracks the provider used in the current session
    val idToken: String? = null, // Firebase ID Token for REST-based flows
    val refreshToken: String? = null // Firebase Refresh Token to get new idTokens
) {
    /**
     * Checks if a specific custom service is linked.
     */
    fun isCustomServiceLinked(serviceName: String): Boolean = linkedServices[serviceName] ?: false

    /**
     * Checks if a specific auth provider is linked.
     */
    fun isAuthProviderLinked(provider: String): Boolean = authProviders[provider] ?: false
}
