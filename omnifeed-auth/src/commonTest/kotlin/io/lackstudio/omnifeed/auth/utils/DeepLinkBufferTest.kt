package io.lackstudio.omnifeed.auth.utils

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeepLinkBufferTest {

    @BeforeTest
    fun setup() {
        // Ensure the buffer is clean before each test
        DeepLinkBuffer.consumeDeepLink()
    }

    @Test
    fun initialValue_is_null() {
        assertNull(DeepLinkBuffer.deepLinkUrl.value)
    }

    @Test
    fun setDeepLink_updates_value() {
        val url = "omnifeed://auth?code=123"
        DeepLinkBuffer.setDeepLink(url)
        assertEquals(url, DeepLinkBuffer.deepLinkUrl.value)
    }

    @Test
    fun consumeDeepLink_resets_value_to_null() {
        val url = "omnifeed://auth?code=123"
        DeepLinkBuffer.setDeepLink(url)
        assertEquals(url, DeepLinkBuffer.deepLinkUrl.value)

        DeepLinkBuffer.consumeDeepLink()
        assertNull(DeepLinkBuffer.deepLinkUrl.value)
    }

    @Test
    fun setDeepLink_multiple_times_updates_to_latest() {
        val url1 = "omnifeed://auth?code=1"
        val url2 = "omnifeed://auth?code=2"

        DeepLinkBuffer.setDeepLink(url1)
        assertEquals(url1, DeepLinkBuffer.deepLinkUrl.value)

        DeepLinkBuffer.setDeepLink(url2)
        assertEquals(url2, DeepLinkBuffer.deepLinkUrl.value)
    }
}
