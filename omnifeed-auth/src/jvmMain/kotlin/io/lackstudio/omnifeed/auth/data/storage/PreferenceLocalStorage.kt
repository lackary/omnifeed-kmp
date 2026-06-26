package io.lackstudio.omnifeed.auth.data.storage

import java.util.prefs.Preferences

/**
 * JVM Implementation of [LocalStorage] using [java.util.prefs.Preferences].
 * Suitable for non-sensitive data like Firebase internal configurations.
 */
class PreferenceLocalStorage(private val prefs: Preferences) : LocalStorage {

    // ==========================================
    // Synchronous Version (Direct)
    // ==========================================

    override fun saveStringDirect(key: String, value: String) {
        prefs.put(key, value)
        prefs.flush()
    }

    override fun getStringDirect(key: String, default: String): String {
        return prefs.get(key, default)
    }

    override fun getStringDirectOrNull(key: String): String? {
        return prefs.get(key, null)
    }

    override fun saveIntDirect(key: String, value: Int) {
        prefs.putInt(key, value)
        prefs.flush()
    }

    override fun getIntDirect(key: String, default: Int): Int {
        return prefs.getInt(key, default)
    }

    override fun getIntDirectOrNull(key: String): Int? {
        val value = prefs.get(key, null)
        return value?.toIntOrNull()
    }

    override fun saveLongDirect(key: String, value: Long) {
        prefs.putLong(key, value)
        prefs.flush()
    }

    override fun getLongDirect(key: String, default: Long): Long {
        return prefs.getLong(key, default)
    }

    override fun getLongDirectOrNull(key: String): Long? {
        val value = prefs.get(key, null)
        return value?.toLongOrNull()
    }

    override fun saveFloatDirect(key: String, value: Float) {
        prefs.putFloat(key, value)
        prefs.flush()
    }

    override fun getFloatDirect(key: String, default: Float): Float {
        return prefs.getFloat(key, default)
    }

    override fun getFloatDirectOrNull(key: String): Float? {
        val value = prefs.get(key, null)
        return value?.toFloatOrNull()
    }

    override fun saveBooleanDirect(key: String, value: Boolean) {
        prefs.putBoolean(key, value)
        prefs.flush()
    }

    override fun getBooleanDirect(key: String, default: Boolean): Boolean {
        return prefs.getBoolean(key, default)
    }

    override fun deleteDirect(key: String) {
        prefs.remove(key)
        prefs.flush()
    }

    override fun clearAllDirect() {
        prefs.clear() // Clear all keys in this node
        prefs.flush()
    }

    // ==========================================
    // Asynchronous Version (Suspend)
    // On JVM, Preferences is fast enough to run on current thread, 
    // or we can just delegate to Direct methods.
    // ==========================================

    override suspend fun saveString(key: String, value: String) = saveStringDirect(key, value)
    override suspend fun getString(key: String, default: String): String = getStringDirect(key, default)
    override suspend fun getStringOrNull(key: String): String? = getStringDirectOrNull(key)

    override suspend fun saveInt(key: String, value: Int) = saveIntDirect(key, value)
    override suspend fun getInt(key: String, default: Int): Int = getIntDirect(key, default)
    override suspend fun getIntOrNull(key: String): Int? = getIntDirectOrNull(key)

    override suspend fun saveLong(key: String, value: Long) = saveLongDirect(key, value)
    override suspend fun getLong(key: String, default: Long): Long = getLongDirect(key, default)
    override suspend fun getLongOrNull(key: String): Long? = getLongDirectOrNull(key)

    override suspend fun saveFloat(key: String, value: Float) = saveFloatDirect(key, value)
    override suspend fun getFloat(key: String, default: Float): Float = getFloatDirect(key, default)
    override suspend fun getFloatOrNull(key: String): Float? = getFloatDirectOrNull(key)

    override suspend fun saveBoolean(key: String, value: Boolean) = saveBooleanDirect(key, value)
    override suspend fun getBoolean(key: String, default: Boolean): Boolean = getBooleanDirect(key, default)

    override suspend fun delete(key: String) = deleteDirect(key)

    override suspend fun clearAll() = clearAllDirect()
}
