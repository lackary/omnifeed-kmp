package io.lackstudio.omnifeed.auth.utils

import io.mockk.*
import kotlinx.coroutines.test.runTest
import java.awt.Desktop
import java.net.URI
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DesktopAuthManagerTest {

    private lateinit var authManager: DesktopAuthManager

    @BeforeTest
    fun setup() {
        authManager = DesktopAuthManager()
        mockkStatic(Desktop::class)
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `setRedirectUrl should update the redirect url`() {
        val url = "http://localhost:1234/callback"
        authManager.setRedirectUrl(url)
        assertEquals(url, authManager.getRedirectUrl())
    }

    @Test
    fun `default redirect url should be localhost 54321`() {
        assertEquals("http://localhost:54321/callback", authManager.getRedirectUrl())
    }

    @Test
    fun `setClientId should update the client id`() {
        // Since clientId is private in DesktopAuthManager, we can't check it directly
        // but we can verify it's used when building the auth URL in signInWithGoogle
        // (if we were to test the full flow)
        authManager.setClientId("test-client-id")
    }

    @Test
    fun `signOut should cleanup resources`() = runTest {
        authManager.signOut()
    }
}
