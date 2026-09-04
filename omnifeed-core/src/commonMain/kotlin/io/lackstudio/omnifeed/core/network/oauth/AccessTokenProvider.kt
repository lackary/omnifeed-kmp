package io.lackstudio.omnifeed.core.network.oauth

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Data class representing an authorization token.
 */
data class AuthToken(
    val type: String,
    val value: String
)

/**
 * AccessTokenProvider manages the authorization token used by Ktor.
 * 
 * It supports both a static initial token (e.g., Client-ID for Unsplash public API)
 * and dynamic OAuth2 tokens (e.g., Bearer tokens for logged-in users).
 */
class AccessTokenProvider(
    initialTokenType: String,
    initialToken: String,
    private val dynamicTokenResolver: (suspend () -> AuthToken?)? = null
) {
    private val logger = Logger.withTag("AccessTokenProvider")

    private val _authToken = MutableStateFlow(
        AuthToken(initialTokenType, initialToken)
    )
    val authToken: StateFlow<AuthToken> = _authToken.asStateFlow()

    private val publicToken = AuthToken(initialTokenType, initialToken)

    /**
     * Updates the current token. Typically called after a successful OAuth flow.
     */
    fun setOAuthToken(newType: String, newValue: String) {
        logger.i { "setOAuthToken: $newType" }
        _authToken.value = AuthToken(type = newType, value = newValue)
    }

    /**
     * Resets the token to the initial public token.
     */
    fun resetToPublic() {
        _authToken.value = publicToken
    }

    /**
     * Returns the latest token. If a dynamic resolver is provided, it attempts to
     * fetch the most up-to-date token (e.g., from AuthRepository).
     */
    suspend fun resolveToken(): AuthToken {
        val dynamicToken = dynamicTokenResolver?.invoke()
        return dynamicToken ?: _authToken.value
    }

    /**
     * Helper for Ktor to get the formatted Authorization header string.
     */
    suspend fun getAuthorizationHeader(): String {
        val auth = resolveToken()
        return "${auth.type} ${auth.value}"
    }
}
