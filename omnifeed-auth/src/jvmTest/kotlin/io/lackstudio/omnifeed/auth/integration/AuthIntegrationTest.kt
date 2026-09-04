package io.lackstudio.omnifeed.auth.integration

import io.lackstudio.omnifeed.auth.config.BuildKonfig
import io.lackstudio.omnifeed.auth.platform.initializeFirebase
import io.lackstudio.omnifeed.auth.data.storage.LocalStorage
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class AuthIntegrationTest {

    private val mockLocalStorage = object : LocalStorage {
        private val data = mutableMapOf<String, String>()
        override suspend fun saveString(key: String, value: String) { data[key] = value }
        override suspend fun getString(key: String, default: String): String = data[key] ?: default
        override suspend fun getStringOrNull(key: String): String? = data[key]
        override suspend fun saveInt(key: String, value: Int) { data[key] = value.toString() }
        override suspend fun getInt(key: String, default: Int): Int = data[key]?.toInt() ?: default
        override suspend fun getIntOrNull(key: String): Int? = data[key]?.toInt()
        override suspend fun saveLong(key: String, value: Long) { data[key] = value.toString() }
        override suspend fun getLong(key: String, default: Long): Long = data[key]?.toLong() ?: default
        override suspend fun getLongOrNull(key: String): Long? = data[key]?.toLong()
        override suspend fun saveFloat(key: String, value: Float) { data[key] = value.toString() }
        override suspend fun getFloat(key: String, default: Float): Float = data[key]?.toFloat() ?: default
        override suspend fun getFloatOrNull(key: String): Float? = data[key]?.toFloat()
        override suspend fun saveBoolean(key: String, value: Boolean) { data[key] = value.toString() }
        override suspend fun getBoolean(key: String, default: Boolean): Boolean = data[key]?.toBoolean() ?: default
        override suspend fun delete(key: String) { data.remove(key) }
        override suspend fun clearAll() { data.clear() }

        override fun saveStringDirect(key: String, value: String) { data[key] = value }
        override fun getStringDirect(key: String, default: String): String = data[key] ?: default
        override fun getStringDirectOrNull(key: String): String? = data[key]
        override fun saveIntDirect(key: String, value: Int) { data[key] = value.toString() }
        override fun getIntDirect(key: String, default: Int): Int = data[key]?.toInt() ?: default
        override fun getIntDirectOrNull(key: String): Int? = data[key]?.toInt()
        override fun saveLongDirect(key: String, value: Long) { data[key] = value.toString() }
        override fun getLongDirect(key: String, default: Long): Long = data[key]?.toLong() ?: default
        override fun getLongDirectOrNull(key: String): Long? = data[key]?.toLong()
        override fun saveFloatDirect(key: String, value: Float) { data[key] = value.toString() }
        override fun getFloatDirect(key: String, default: Float): Float = data[key]?.toFloat() ?: default
        override fun getFloatDirectOrNull(key: String): Float? = data[key]?.toFloat()
        override fun saveBooleanDirect(key: String, value: Boolean) { data[key] = value.toString() }
        override fun getBooleanDirect(key: String, default: Boolean): Boolean = data[key]?.toBoolean() ?: default
        override fun deleteDirect(key: String) { data.remove(key) }
        override fun clearAllDirect() { data.clear() }
    }

    @BeforeTest
    fun setup() {
        if (BuildKonfig.FIREBASE_WEB_BASE64.isNotEmpty()) {
            try {
                initializeFirebase(BuildKonfig.FIREBASE_WEB_BASE64, mockLocalStorage)
            } catch (e: Exception) {
                // Ignore if already initialized
            }
        }
    }

    @Test
    fun `firebase should be initialized`() = runTest {
        if (BuildKonfig.FIREBASE_WEB_BASE64.isEmpty()) {
            println("Skipping test: FIREBASE_WEB_BASE64 is empty")
            return@runTest
        }
        
        val auth = Firebase.auth
        assertNotNull(auth)
    }
}
