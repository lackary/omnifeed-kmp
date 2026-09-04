package io.lackstudio.omnifeed.auth.data.storage

import eu.anifantakis.lib.ksafe.KSafe

/**
 * Implementation of the KMP LocalStorage core interface.
 * Uses KSafe as the underlying encrypted storage (supports cross-platform hardware-level encryption).
 */
class KSafeLocalStorage(
    private val kSafe: KSafe
) : LocalStorage {

    // ==========================================
    // Asynchronous Version (Suspend)
    // ==========================================

    // ==== String ====
    override suspend fun saveString(key: String, value: String) {
        kSafe.put(key, value)
    }

    override suspend fun getString(key: String, default: String): String {
        return kSafe.get(key, default)
    }

    override suspend fun getStringOrNull(key: String): String? {
        // Follows the original logic: treat empty strings as no data.
        return kSafe.get(key, "").takeIf { it.isNotEmpty() }
    }

    // ==== Int ====
    override suspend fun saveInt(key: String, value: Int) {
        kSafe.put(key, value)
    }

    override suspend fun getInt(key: String, default: Int): Int {
        return kSafe.get(key, default)
    }

    override suspend fun getIntOrNull(key: String): Int? {
        // Use Int.MIN_VALUE as a "Sentinel Value"
        // Returning MIN_VALUE indicates that the underlying data does not exist.
        val value = kSafe.get(key, Int.MIN_VALUE)
        return if (value == Int.MIN_VALUE) null else value
    }

    // ==== Long ====
    override suspend fun saveLong(key: String, value: Long) {
        kSafe.put(key, value)
    }

    override suspend fun getLong(key: String, default: Long): Long {
        return kSafe.get(key, default)
    }

    override suspend fun getLongOrNull(key: String): Long? {
        val value = kSafe.get(key, Long.MIN_VALUE)
        return if (value == Long.MIN_VALUE) null else value
    }

    // ==== Float ====
    override suspend fun saveFloat(key: String, value: Float) {
        kSafe.put(key, value)
    }

    override suspend fun getFloat(key: String, default: Float): Float {
        return kSafe.get(key, default)
    }

    override suspend fun getFloatOrNull(key: String): Float? {
        val value = kSafe.get(key, Float.MIN_VALUE)
        return if (value == Float.MIN_VALUE) null else value
    }

    // ==== Boolean ====
    override suspend fun saveBoolean(key: String, value: Boolean) {
        kSafe.put(key, value)
    }

    override suspend fun getBoolean(key: String, default: Boolean): Boolean {
        return kSafe.get(key, default)
    }

    // ==== Delete (Suspend) ====
    override suspend fun delete(key: String) {
        kSafe.delete(key)
    }

    override suspend fun clearAll() {
        kSafe.clearAll()
    }

    override fun clearAllDirect() {
        // KSafe doesn't provide a direct/synchronous clearAll as it involves 
        // complex Keystore operations and write coalescing.
        // For now, we can only support the suspend version reliably.
    }


    // ==========================================
    // Synchronous Version (Direct)
    // ==========================================

    // ==== String Direct ====
    override fun saveStringDirect(key: String, value: String) {
        kSafe.putDirect(key, value)
    }

    override fun getStringDirect(key: String, default: String): String {
        return kSafe.getDirect(key, default)
    }

    override fun getStringDirectOrNull(key: String): String? {
        return kSafe.getDirect(key, "").ifEmpty { null }
    }

    // ==== Int Direct ====
    override fun saveIntDirect(key: String, value: Int) {
        kSafe.putDirect(key, value)
    }

    override fun getIntDirect(key: String, default: Int): Int {
        return kSafe.getDirect(key, default)
    }

    override fun getIntDirectOrNull(key: String): Int? {
        val value = kSafe.getDirect(key, Int.MIN_VALUE)
        return if (value == Int.MIN_VALUE) null else value
    }

    // ==== Long Direct ====
    override fun saveLongDirect(key: String, value: Long) {
        kSafe.putDirect(key, value)
    }

    override fun getLongDirect(key: String, default: Long): Long {
        return kSafe.getDirect(key, default)
    }

    override fun getLongDirectOrNull(key: String): Long? {
        val value = kSafe.getDirect(key, Long.MIN_VALUE)
        return if (value == Long.MIN_VALUE) null else value
    }

    // ==== Float Direct ====
    override fun saveFloatDirect(key: String, value: Float) {
        kSafe.putDirect(key, value)
    }

    override fun getFloatDirect(key: String, default: Float): Float {
        return kSafe.getDirect(key, default)
    }

    override fun getFloatDirectOrNull(key: String): Float? {
        val value = kSafe.getDirect(key, Float.MIN_VALUE)
        return if (value == Float.MIN_VALUE) null else value
    }

    // ==== Boolean Direct ====
    override fun saveBooleanDirect(key: String, value: Boolean) {
        kSafe.putDirect(key, value)
    }

    override fun getBooleanDirect(key: String, default: Boolean): Boolean {
        return kSafe.getDirect(key, default)
    }

    // ==== Delete Direct ====
    override fun deleteDirect(key: String) {
        kSafe.deleteDirect(key)
    }
}
