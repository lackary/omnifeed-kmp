package io.lackstudio.omnifeed.auth.utils

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IosAuthManagerTest {

    private lateinit var authManager: IosAuthManager

    @BeforeTest
    fun setup() {
        authManager = IosAuthManager()
    }

    @Test
    fun `setRedirectUrl should update the redirect url`() {
        val url = "omnifeed://auth"
        authManager.setRedirectUrl(url)
        assertEquals(url, authManager.getRedirectUrl())
    }

    @Test
    fun `default redirect url should be omnihub callback`() {
        assertEquals("omnihub://auth/callback", authManager.getRedirectUrl())
    }
}
