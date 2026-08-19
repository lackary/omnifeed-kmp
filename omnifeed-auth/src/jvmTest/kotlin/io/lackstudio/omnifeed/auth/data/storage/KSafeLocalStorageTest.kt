package io.lackstudio.omnifeed.auth.data.storage

import eu.anifantakis.lib.ksafe.KSafe
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class KSafeLocalStorageTest {

    private val kSafe = mockk<KSafe>(relaxed = true)
    private lateinit var localStorage: KSafeLocalStorage

    @BeforeTest
    fun setup() {
        localStorage = KSafeLocalStorage(kSafe)
    }

    // ==========================================
    // Asynchronous Version (Suspend) Tests
    // ==========================================

    @Test
    fun `saveString should call kSafe put`() = runTest {
        val key = "key"
        val value = "value"
        localStorage.saveString(key, value)
        coVerify { kSafe.put(key, value) }
    }

    @Test
    fun `getString should return value from kSafe`() = runTest {
        val key = "key"
        val default = "default"
        coEvery { kSafe.get(key, default) } returns "stored"
        assertEquals("stored", localStorage.getString(key, default))
    }

    @Test
    fun `getStringOrNull should return null if kSafe returns empty string`() = runTest {
        coEvery { kSafe.get(any(), "") } returns ""
        assertNull(localStorage.getStringOrNull("key"))
    }

    @Test
    fun `saveInt should call kSafe put`() = runTest {
        localStorage.saveInt("key", 1)
        coVerify { kSafe.put("key", 1) }
    }

    @Test
    fun `getInt should return value from kSafe`() = runTest {
        coEvery { kSafe.get("key", 0) } returns 100
        assertEquals(100, localStorage.getInt("key", 0))
    }

    @Test
    fun `getIntOrNull should return null if kSafe returns Int MIN_VALUE`() = runTest {
        coEvery { kSafe.get(any(), Int.MIN_VALUE) } returns Int.MIN_VALUE
        assertNull(localStorage.getIntOrNull("key"))
    }

    @Test
    fun `saveLong should call kSafe put`() = runTest {
        localStorage.saveLong("key", 1L)
        coVerify { kSafe.put("key", 1L) }
    }

    @Test
    fun `getLong should return value from kSafe`() = runTest {
        coEvery { kSafe.get("key", 0L) } returns 100L
        assertEquals(100L, localStorage.getLong("key", 0L))
    }

    @Test
    fun `getLongOrNull should return null if kSafe returns Long MIN_VALUE`() = runTest {
        coEvery { kSafe.get(any(), Long.MIN_VALUE) } returns Long.MIN_VALUE
        assertNull(localStorage.getLongOrNull("key"))
    }

    @Test
    fun `saveFloat should call kSafe put`() = runTest {
        localStorage.saveFloat("key", 1.0f)
        coVerify { kSafe.put("key", 1.0f) }
    }

    @Test
    fun `getFloat should return value from kSafe`() = runTest {
        coEvery { kSafe.get("key", 0.0f) } returns 1.5f
        assertEquals(1.5f, localStorage.getFloat("key", 0.0f))
    }

    @Test
    fun `getFloatOrNull should return null if kSafe returns Float MIN_VALUE`() = runTest {
        coEvery { kSafe.get(any(), Float.MIN_VALUE) } returns Float.MIN_VALUE
        assertNull(localStorage.getFloatOrNull("key"))
    }

    @Test
    fun `saveBoolean should call kSafe put`() = runTest {
        localStorage.saveBoolean("key", true)
        coVerify { kSafe.put("key", true) }
    }

    @Test
    fun `getBoolean should return value from kSafe`() = runTest {
        coEvery { kSafe.get("key", false) } returns true
        assertTrue(localStorage.getBoolean("key", false))
    }

    @Test
    fun `delete should call kSafe delete`() = runTest {
        localStorage.delete("key")
        coVerify { kSafe.delete("key") }
    }

    @Test
    fun `clearAll should call kSafe clearAll`() = runTest {
        localStorage.clearAll()
        coVerify { kSafe.clearAll() }
    }

    // ==========================================
    // Synchronous Version (Direct) Tests
    // ==========================================

    @Test
    fun `saveStringDirect should call kSafe putDirect`() {
        localStorage.saveStringDirect("key", "value")
        verify { kSafe.putDirect("key", "value") }
    }

    @Test
    fun `getStringDirect should return value from kSafe`() {
        every { kSafe.getDirect("key", "default") } returns "stored"
        assertEquals("stored", localStorage.getStringDirect("key", "default"))
    }

    @Test
    fun `getStringDirectOrNull should return null if empty`() {
        every { kSafe.getDirect(any(), "") } returns ""
        assertNull(localStorage.getStringDirectOrNull("key"))
    }

    @Test
    fun `saveIntDirect should call kSafe putDirect`() {
        localStorage.saveIntDirect("key", 1)
        verify { kSafe.putDirect("key", 1) }
    }

    @Test
    fun `getIntDirect should return value`() {
        every { kSafe.getDirect("key", 0) } returns 100
        assertEquals(100, localStorage.getIntDirect("key", 0))
    }

    @Test
    fun `getIntDirectOrNull should return null if MIN_VALUE`() {
        every { kSafe.getDirect(any(), Int.MIN_VALUE) } returns Int.MIN_VALUE
        assertNull(localStorage.getIntDirectOrNull("key"))
    }

    @Test
    fun `saveLongDirect should call kSafe putDirect`() {
        localStorage.saveLongDirect("key", 1L)
        verify { kSafe.putDirect("key", 1L) }
    }

    @Test
    fun `getLongDirect should return value`() {
        every { kSafe.getDirect("key", 0L) } returns 100L
        assertEquals(100L, localStorage.getLongDirect("key", 0L))
    }

    @Test
    fun `getLongDirectOrNull should return null if MIN_VALUE`() {
        every { kSafe.getDirect(any(), Long.MIN_VALUE) } returns Long.MIN_VALUE
        assertNull(localStorage.getLongDirectOrNull("key"))
    }

    @Test
    fun `saveFloatDirect should call kSafe putDirect`() {
        localStorage.saveFloatDirect("key", 1.0f)
        verify { kSafe.putDirect("key", 1.0f) }
    }

    @Test
    fun `getFloatDirect should return value`() {
        every { kSafe.getDirect("key", 0.0f) } returns 1.5f
        assertEquals(1.5f, localStorage.getFloatDirect("key", 0.0f))
    }

    @Test
    fun `getFloatDirectOrNull should return null if MIN_VALUE`() {
        every { kSafe.getDirect(any(), Float.MIN_VALUE) } returns Float.MIN_VALUE
        assertNull(localStorage.getFloatDirectOrNull("key"))
    }

    @Test
    fun `saveBooleanDirect should call kSafe putDirect`() {
        localStorage.saveBooleanDirect("key", true)
        verify { kSafe.putDirect("key", true) }
    }

    @Test
    fun `getBooleanDirect should return value`() {
        every { kSafe.getDirect("key", false) } returns true
        assertTrue(localStorage.getBooleanDirect("key", false))
    }

    @Test
    fun `deleteDirect should call kSafe deleteDirect`() {
        localStorage.deleteDirect("key")
        verify { kSafe.deleteDirect("key") }
    }

    @Test
    fun `clearAllDirect should execute without error`() {
        // clearAllDirect in KSafeLocalStorage is currently an empty implementation
        // verifying it doesn't crash.
        localStorage.clearAllDirect()
    }
}
