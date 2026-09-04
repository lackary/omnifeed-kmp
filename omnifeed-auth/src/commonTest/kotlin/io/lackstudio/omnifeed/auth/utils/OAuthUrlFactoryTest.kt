package io.lackstudio.omnifeed.auth.utils

import kotlin.test.Test
import kotlin.test.assertTrue

class OAuthUrlFactoryTest {

    private val baseUrl = "https://accounts.google.com/o/oauth2/v2/auth"
    private val clientId = "test-client-id"
    private val redirectUri = "https://example.com/callback"

    @Test
    fun buildAuthUrl_withMandatoryParameters_returnsCorrectUrl() {
        val url = OAuthUrlFactory.buildAuthUrl(
            baseUrl = baseUrl,
            clientId = clientId,
            redirectUri = redirectUri
        )

        assertTrue(url.startsWith(baseUrl))
        assertTrue(url.contains("client_id=$clientId"))
        assertTrue(url.contains("response_type=code"))
        assertTrue(url.contains("redirect_uri=https%3A%2F%2Fexample.com%2Fcallback"))
    }

    @Test
    fun buildAuthUrl_withScope_returnsUrlWithScope() {
        val scopes = listOf("email", "profile", "openid")
        val url = OAuthUrlFactory.buildAuthUrl(
            baseUrl = baseUrl,
            clientId = clientId,
            redirectUri = redirectUri,
            scope = scopes
        )

        assertTrue(url.contains("scope=email+profile+openid"))
    }

    @Test
    fun buildAuthUrl_withState_returnsUrlWithState() {
        val state = "random-state-string"
        val url = OAuthUrlFactory.buildAuthUrl(
            baseUrl = baseUrl,
            clientId = clientId,
            redirectUri = redirectUri,
            state = state
        )

        assertTrue(url.contains("state=$state"))
    }

    @Test
    fun buildAuthUrl_withCustomResponseType_returnsUrlWithCustomResponseType() {
        val responseType = "token"
        val url = OAuthUrlFactory.buildAuthUrl(
            baseUrl = baseUrl,
            clientId = clientId,
            redirectUri = redirectUri,
            responseType = responseType
        )

        assertTrue(url.contains("response_type=$responseType"))
    }

    @Test
    fun buildAuthUrl_withAllParameters_returnsCompleteUrl() {
        val scopes = listOf("email")
        val state = "xyz123"
        val responseType = "code"
        
        val url = OAuthUrlFactory.buildAuthUrl(
            baseUrl = baseUrl,
            clientId = clientId,
            redirectUri = redirectUri,
            scope = scopes,
            state = state,
            responseType = responseType
        )

        assertTrue(url.startsWith(baseUrl))
        assertTrue(url.contains("client_id=$clientId"))
        assertTrue(url.contains("redirect_uri=https%3A%2F%2Fexample.com%2Fcallback"))
        assertTrue(url.contains("scope=email"))
        assertTrue(url.contains("state=$state"))
        assertTrue(url.contains("response_type=$responseType"))
    }
}
