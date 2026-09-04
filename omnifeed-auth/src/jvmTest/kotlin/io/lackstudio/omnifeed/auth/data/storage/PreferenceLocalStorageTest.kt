package io.lackstudio.omnifeed.auth.data.storage

import io.mockk.*
import kotlinx.coroutines.test.runTest
import java.util.prefs.Preferences
import kotlin.test.*

class PreferenceLocalStorageTest {

    private val prefs = mockk<Preferences>(relaxed = true)
    private lateinit var localStorage: PreferenceLocalStorage

    @BeforeTest
    fun setup() {
        localStorage = PreferenceLocalStorage(prefs)
    }

    // ==========================================
    // Synchronous Version (Direct) Tests
    // ==========================================

    @Test
    fun `saveStringDirect should call put and flush`() {
        localStorage.saveStringDirect("key", "value")
        verify { prefs.put("key", "value") }
        verify { prefs.flush() }
    }

    @Test
    fun `getStringDirect should return value from prefs`() {
        every { prefs.get("key", "default") } returns "stored"
        assertEquals("stored", localStorage.getStringDirect("key", "default"))
    }

    @Test
    fun `getStringDirectOrNull should return value from prefs`() {
        every { prefs.get("key", null) } returns "stored"
        assertEquals("stored", localStorage.getStringDirectOrNull("key"))
    }

    @Test
    fun `getStringDirectOrNull should return null if key missing`() {
        every { prefs.get("key", null) } returns null
        assertNull(localStorage.getStringDirectOrNull("key"))
    }

    @Test
    fun `saveIntDirect should call putInt and flush`() {
        localStorage.saveIntDirect("key", 42)
        verify { prefs.putInt("key", 42) }
        verify { prefs.flush() }
    }

    @Test
    fun `getIntDirect should return value from prefs`() {
        every { prefs.getInt("key", 0) } returns 42
        assertEquals(42, localStorage.getIntDirect("key", 0))
    }

    @Test
    fun `getIntDirectOrNull should return Int from string value`() {
        every { prefs.get("key", null) } returns "42"
        assertEquals(42, localStorage.getIntDirectOrNull("key"))
    }

    @Test
    fun `getIntDirectOrNull should return null if invalid format`() {
        every { prefs.get("key", null) } returns "not-an-int"
        assertNull(localStorage.getIntDirectOrNull("key"))
    }

    @Test
    fun `saveLongDirect should call putLong and flush`() {
        localStorage.saveLongDirect("key", 100L)
        verify { prefs.putLong("key", 100L) }
        verify { prefs.flush() }
    }

    @Test
    fun `getLongDirect should return value from prefs`() {
        every { prefs.getLong("key", 0L) } returns 100L
        assertEquals(100L, localStorage.getLongDirect("key", 0L))
    }

    @Test
    fun `getLongDirectOrNull should return Long from string value`() {
        every { prefs.get("key", null) } returns "100"
        assertEquals(100L, localStorage.getLongDirectOrNull("key"))
    }

    @Test
    fun `getLongDirectOrNull should return null if invalid format`() {
        every { prefs.get("key", null) } returns "not-a-long"
        assertNull(localStorage.getLongDirectOrNull("key"))
    }

    @Test
    fun `saveFloatDirect should call putFloat and flush`() {
        localStorage.saveFloatDirect("key", 1.5f)
        verify { prefs.putFloat("key", 1.5f) }
        verify { prefs.flush() }
    }

    @Test
    fun `getFloatDirect should return value from prefs`() {
        every { prefs.getFloat("key", 0f) } returns 1.5f
        assertEquals(1.5f, localStorage.getFloatDirect("key", 0f))
    }

    @Test
    fun `getFloatDirectOrNull should return Float from string value`() {
        every { prefs.get(any(), null) } returns "1.5"
        assertEquals(1.5f, localStorage.getFloatDirectOrNull("key"))
    }

    @Test
    fun `getFloatDirectOrNull should return null if invalid format`() {
        every { prefs.get("key", null) } returns "not-a-float"
        assertNull(localStorage.getFloatDirectOrNull("key"))
    }

    @Test
    fun `saveBooleanDirect should call putBoolean and flush`() {
        localStorage.saveBooleanDirect("key", true)
        verify { prefs.putBoolean("key", true) }
        verify { prefs.flush() }
    }

    @Test
    fun `getBooleanDirect should return value from prefs`() {
        every { prefs.getBoolean("key", false) } returns true
        assertTrue(localStorage.getBooleanDirect("key", false))
    }

    @Test
    fun `deleteDirect should call remove and flush`() {
        localStorage.deleteDirect("key")
        verify { prefs.remove("key") }
        verify { prefs.flush() }
    }

    @Test
    fun `clearAllDirect should call clear and flush`() {
        localStorage.clearAllDirect()
        verify { prefs.clear() }
        verify { prefs.flush() }
    }

    // ==========================================
    // Asynchronous Version (Suspend) Tests
    // ==========================================

    @Test
    fun `saveString should delegate to saveStringDirect`() = runTest {
        localStorage.saveString("key", "value")
        verify { prefs.put("key", "value") }
        verify { prefs.flush() }
    }

    @Test
    fun `getString should return value from prefs`() = runTest {
        every { prefs.get("key", "default") } returns "stored"
        assertEquals("stored", localStorage.getString("key", "default"))
    }

    @Test
    fun `getStringOrNull should return value from prefs`() = runTest {
        every { prefs.get("key", null) } returns "stored"
        assertEquals("stored", localStorage.getStringOrNull("key"))
    }

    @Test
    fun `saveInt should delegate to saveIntDirect`() = runTest {
        localStorage.saveInt("key", 42)
        verify { prefs.putInt("key", 42) }
        verify { prefs.flush() }
    }

    @Test
    fun `getInt should return value from prefs`() = runTest {
        every { prefs.getInt("key", 0) } returns 42
        assertEquals(42, localStorage.getInt("key", 0))
    }

    @Test
    fun `getIntOrNull should return Int from string value`() = runTest {
        every { prefs.get("key", null) } returns "42"
        assertEquals(42, localStorage.getIntOrNull("key"))
    }

    @Test
    fun `saveLong should delegate to saveLongDirect`() = runTest {
        localStorage.saveLong("key", 100L)
        verify { prefs.putLong("key", 100L) }
        verify { prefs.flush() }
    }

    @Test
    fun `getLong should return value from prefs`() = runTest {
        every { prefs.getLong("key", 0L) } returns 100L
        assertEquals(100L, localStorage.getLong("key", 0L))
    }

    @Test
    fun `getLongOrNull should return Long from string value`() = runTest {
        every { prefs.get("key", null) } returns "100"
        assertEquals(100L, localStorage.getLongOrNull("key"))
    }

    @Test
    fun `saveFloat should delegate to saveFloatDirect`() = runTest {
        localStorage.saveFloat("key", 1.5f)
        verify { prefs.putFloat("key", 1.5f) }
        verify { prefs.flush() }
    }

    @Test
    fun `getFloat should return value from prefs`() = runTest {
        every { prefs.getFloat("key", 0f) } returns 1.5f
        assertEquals(1.5f, localStorage.getFloat("key", 0f))
    }

    @Test
    fun `getFloatOrNull should return Float from string value`() = runTest {
        every { prefs.get("key", null) } returns "1.5"
        assertEquals(1.5f, localStorage.getFloatOrNull("key"))
    }

    @Test
    fun `saveBoolean should delegate to saveBooleanDirect`() = runTest {
        localStorage.saveBoolean("key", true)
        verify { prefs.putBoolean("key", true) }
        verify { prefs.flush() }
    }

    @Test
    fun `getBoolean should return value from prefs`() = runTest {
        every { prefs.getBoolean("key", false) } returns true
        assertTrue(localStorage.getBoolean("key", false))
    }

    @Test
    fun `delete should delegate to deleteDirect`() = runTest {
        localStorage.delete("key")
        verify { prefs.remove("key") }
        verify { prefs.flush() }
    }

    @Test
    fun `clearAll should delegate to clearAllDirect`() = runTest {
        localStorage.clearAll()
        verify { prefs.clear() }
        verify { prefs.flush() }
    }
}
