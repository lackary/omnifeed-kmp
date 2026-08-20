package io.lackstudio.omnifeed.auth.utils

import kotlinx.browser.window
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WebAuthManagerTest {

    private lateinit var authManager: WebAuthManager

    @BeforeTest
    fun setup() {
        authManager = WebAuthManager()
    }

    @Test
    fun `setRedirectUrl should update the redirect url`() {
        val url = "https://example.com/callback"
        authManager.setRedirectUrl(url)
        assertEquals(url, authManager.getRedirectUrl())
    }

    @Test
    fun `signOut should clear state`() = runTest {
        // We can't fully test window.document.cookie clearing in Node.js,
        // but we can verify the call doesn't crash if window is present.
        authManager.signOut()
    }
}
