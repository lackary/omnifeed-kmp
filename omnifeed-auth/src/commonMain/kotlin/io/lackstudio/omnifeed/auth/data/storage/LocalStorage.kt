package io.lackstudio.omnifeed.auth.data.storage

/**
 * Core Multiplatform Local Storage Interface
 * * ⚠️ Design Guidelines:
 * 1. To ensure KMP cross-platform compilation safety and performance, this interface only supports primitive types and String.
 * 2. Methods with a [default] parameter are guaranteed to return a non-null value.
 * 3. Methods with the [OrNull] suffix are used to detect if a key exists; they return null if no data is found.
 */
interface LocalStorage {

    // ==========================================
    // Asynchronous Version (Suspend) - Recommended for scenarios requiring heavy I/O or DataStore.
    // ==========================================

    suspend fun saveString(key: String, value: String)
    suspend fun getString(key: String, default: String): String
    suspend fun getStringOrNull(key: String): String?

    suspend fun saveInt(key: String, value: Int)
    suspend fun getInt(key: String, default: Int): Int
    suspend fun getIntOrNull(key: String): Int?

    suspend fun saveLong(key: String, value: Long)
    suspend fun getLong(key: String, default: Long): Long
    suspend fun getLongOrNull(key: String): Long?

    suspend fun saveFloat(key: String, value: Float)
    suspend fun getFloat(key: String, default: Float): Float
    suspend fun getFloatOrNull(key: String): Float?

    suspend fun saveBoolean(key: String, value: Boolean)
    suspend fun getBoolean(key: String, default: Boolean): Boolean

    suspend fun delete(key: String)

    /**
     * Clears all data in this storage.
     */
    suspend fun clearAll()


    // ==========================================
    // Synchronous Version (Direct) - Recommended for lightweight scenarios requiring immediate state retrieval (e.g., MMKV, UserDefaults).
    // ==========================================

    fun saveStringDirect(key: String, value: String)
    fun getStringDirect(key: String, default: String): String
    fun getStringDirectOrNull(key: String): String?

    fun saveIntDirect(key: String, value: Int)
    fun getIntDirect(key: String, default: Int): Int
    fun getIntDirectOrNull(key: String): Int?

    fun saveLongDirect(key: String, value: Long)
    fun getLongDirect(key: String, default: Long): Long
    fun getLongDirectOrNull(key: String): Long?

    fun saveFloatDirect(key: String, value: Float)
    fun getFloatDirect(key: String, default: Float): Float
    fun getFloatDirectOrNull(key: String): Float?

    fun saveBooleanDirect(key: String, value: Boolean)
    fun getBooleanDirect(key: String, default: Boolean): Boolean

    fun deleteDirect(key: String)

    /**
     * Clears all data in this storage (Synchronous version).
     */
    fun clearAllDirect()
}
