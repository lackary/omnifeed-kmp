package io.lackstudio.omnifeed.auth.utils

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthManagerTest {

    private lateinit var authManager: FakeAuthManager

    @BeforeTest
    fun setup() {
        authManager = FakeAuthManager()
    }

    @Test
    fun setAndGetRedirectUrl() {
        val url = "https://example.com/callback"
        authManager.setRedirectUrl(url)
        assertEquals(url, authManager.getRedirectUrl())
    }

    @Test
    fun defaultRedirectUrl_whenNotSet() {
        assertEquals("default_redirect_url", authManager.getRedirectUrl())
    }

    @Test
    fun setClientId_updatesState() {
        val clientId = "test-client-id"
        authManager.setClientId(clientId)
        assertEquals(clientId, authManager._clientId)
    }

    @Test
    fun setSuccessHtml_updatesState() {
        val html = "<html><body>Success</body></html>"
        authManager.setSuccessHtml(html)
        assertEquals(html, authManager._successHtml)
    }

    @Test
    fun signOut_clearsState() = runTest {
        authManager.setClientId("id")
        authManager.signOut()
        assertTrue(authManager.isSignedOut)
    }

    @Test
    fun signInWithGoogle_returnsFakeTokens() = runTest {
        val tokens = authManager.signInWithGoogle()
        assertEquals("fake_id_token", tokens.idToken)
        assertEquals("fake_access_token", tokens.accessToken)
    }

    @Test
    fun signInWithOAuthPopup_returnsFakeCode() = runTest {
        val code = authManager.signInWithOAuthPopup("https://auth.url")
        assertEquals("fake_auth_code", code)
    }

    @Test
    fun googleAuthTokens_equality() {
        val tokens1 = GoogleAuthTokens("id", "access")
        val tokens2 = GoogleAuthTokens("id", "access")
        val tokens3 = GoogleAuthTokens("id", "different")

        assertEquals(tokens1, tokens2)
        assertTrue(tokens1 != tokens3)
    }

    /**
     * A Fake implementation of AuthManager for testing purposes.
     */
    private class FakeAuthManager : AuthManager {
        var _redirectUrl: String? = null
        var _clientId: String? = null
        var _successHtml: String? = null
        var isSignedOut: Boolean = false
        var startLoginCalledWith: String? = null

        override fun setRedirectUrl(url: String) {
            this._redirectUrl = url
        }

        override fun setClientId(id: String) {
            this._clientId = id
        }

        override fun setSuccessHtml(html: String) {
            this._successHtml = html
        }

        override fun getRedirectUrl(): String {
            return _redirectUrl ?: "default_redirect_url"
        }

        override fun startLogin(authUrl: String) {
            startLoginCalledWith = authUrl
        }

        override suspend fun signInWithGoogle(context: Any?): GoogleAuthTokens {
            return GoogleAuthTokens(idToken = "fake_id_token", accessToken = "fake_access_token")
        }

        override suspend fun signInWithOAuthPopup(authUrl: String): String {
            return "fake_auth_code"
        }

        override suspend fun signOut() {
            isSignedOut = true
        }
    }
}
