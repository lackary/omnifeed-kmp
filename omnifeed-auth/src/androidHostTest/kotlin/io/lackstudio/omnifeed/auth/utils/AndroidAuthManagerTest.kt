package io.lackstudio.omnifeed.auth.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.credentials.CredentialManager
import io.mockk.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AndroidAuthManagerTest {

    private val context = mockk<Context>(relaxed = true)
    private val credentialManager = mockk<CredentialManager>(relaxed = true)
    private lateinit var authManager: AndroidAuthManager

    @BeforeTest
    fun setup() {
        mockkStatic(Uri::class)
        // Stub Uri.parse to return a mocked Uri object
        val mockUri = mockk<Uri>()
        every { Uri.parse(any()) } returns mockUri
        every { mockUri.toString() } returns "https://example.com/auth"
        
        // Mock Intent constructor
        mockkConstructor(Intent::class)
        
        authManager = AndroidAuthManager(context, credentialManager)
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `setRedirectUrl should update the redirect url`() {
        val url = "omnifeed://auth"
        authManager.setRedirectUrl(url)
        assertEquals(url, authManager.getRedirectUrl())
    }

    @Test
    fun `startLogin should start activity with VIEW intent`() {
        val authUrl = "https://example.com/auth"
        
        // Setup Intent mock behavior
        every { anyConstructed<Intent>().addFlags(any()) } returns mockk()
        every { anyConstructed<Intent>().action } returns Intent.ACTION_VIEW
        every { anyConstructed<Intent>().dataString } returns authUrl

        authManager.startLogin(authUrl)

        verify { context.startActivity(any<Intent>()) }
    }
}
