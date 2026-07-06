package io.lackstudio.omnifeed.auth.utils

import io.ktor.http.URLBuilder

object OAuthUrlFactory {

    /**
     * A generic builder for OAuth 2.0 authorization URLs.
     *
     * @param baseUrl The base URL of the authorization server (required).
     * @param clientId The application client ID (required).
     * @param redirectUri The redirect URI for the callback (highly recommended).
     * @param scope The list of scopes for the access request (optional).
     * @param state A random string used to prevent CSRF attacks (highly recommended).
     * @param responseType The authorization response type, defaults to "code".
     */
    fun buildAuthUrl(
        baseUrl: String,
        clientId: String,
        redirectUri: String,
        scope: List<String> = emptyList(),
        state: String? = null,
        responseType: String = "code"
    ): String {
        return URLBuilder(baseUrl).apply {
            parameters.append("client_id", clientId)
            parameters.append("response_type", responseType)
            parameters.append("redirect_uri", redirectUri)

            if (scope.isNotEmpty()) {
                parameters.append("scope", scope.joinToString(" "))
            }

            state?.let { parameters.append("state", it) }
        }.buildString()
    }
}
