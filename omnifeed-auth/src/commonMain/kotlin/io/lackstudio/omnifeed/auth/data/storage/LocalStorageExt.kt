package io.lackstudio.omnifeed.auth.data.storage

import co.touchlab.kermit.Logger
import io.lackstudio.omnifeed.auth.domain.model.User
import kotlinx.serialization.json.Json
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@PublishedApi
internal val localStorageJson = Json { ignoreUnknownKeys = true }
const val FIREBASE_AUTH_USER_KEY = "firebase_auth_user_key"
val localStorageExtLogger = Logger.withTag("LocalStorageExtLogger")

/**
 * LocalStorage Generic Extension Functions (Syntactic Sugar)
 * Leverages Kotlin's inline reified functions for compile-time type routing.
 * Automatically handles primitives and serializable objects.
 */
// ==========================================
// Asynchronous (Suspend) Generic Extensions
// ==========================================

suspend inline fun <reified T : Any> LocalStorage.save(key: String, value: T?) {
    if (value == null) {
        localStorageExtLogger.d { "Deleting key: $key" }
        delete(key)
        return
    }
    localStorageExtLogger.d { "Saving key: $key (Type: ${T::class.simpleName})" }
    when (value) {
        is String -> saveString(key, value)
        is Int -> saveInt(key, value)
        is Boolean -> saveBoolean(key, value)
        is Float -> saveFloat(key, value)
        is Long -> saveLong(key, value)
        else -> saveString(key, localStorageJson.encodeToString(value))
    }
    localStorageExtLogger.d { "Successfully saved key: $key" }
}

suspend inline fun <reified T : Any> LocalStorage.get(key: String, default: T): T {
    return when (T::class) {
        String::class -> getString(key, default as String) as T
        Int::class -> getInt(key, default as Int) as T
        Boolean::class -> getBoolean(key, default as Boolean) as T
        Float::class -> getFloat(key, default as Float) as T
        Long::class -> getLong(key, default as Long) as T
        else -> {
            getStringOrNull(key)?.let {
                try {
                    localStorageJson.decodeFromString<T>(it)
                } catch (e: Exception) {
                    localStorageExtLogger.e(e) { "Failed to decode key: $key for type: ${T::class.simpleName}" }
                    default
                }
            } ?: default
        }
    }
}

suspend inline fun <reified T : Any> LocalStorage.getOrNull(key: String): T? {
    return when (T::class) {
        String::class -> getStringOrNull(key) as T?
        Int::class -> getIntOrNull(key) as T?
        Float::class -> getFloatOrNull(key) as T?
        Long::class -> getLongOrNull(key) as T?
        else -> {
            getStringOrNull(key)?.let {
                try {
                    localStorageJson.decodeFromString<T>(it)
                } catch (e: Exception) {
                    localStorageExtLogger.e(e) { "Failed to decode key: $key for type: ${T::class.simpleName}" }
                    null
                }
            }
        }
    }
}


// ==========================================
// Synchronous (Direct) Generic Extensions
// ==========================================

inline fun <reified T : Any> LocalStorage.saveDirect(key: String, value: T?) {
    if (value == null) {
        localStorageExtLogger.d { "Deleting key (Direct): $key" }
        deleteDirect(key)
        return
    }
    localStorageExtLogger.d { "Saving key (Direct): $key (Type: ${T::class.simpleName})" }
    when (value) {
        is String -> saveStringDirect(key, value)
        is Int -> saveIntDirect(key, value)
        is Boolean -> saveBooleanDirect(key, value)
        is Float -> saveFloatDirect(key, value)
        is Long -> saveLongDirect(key, value)
        else -> saveStringDirect(key, localStorageJson.encodeToString(value))
    }
    localStorageExtLogger.d { "Successfully saved key (Direct): $key" }
}

inline fun <reified T : Any> LocalStorage.getDirect(key: String, default: T): T {
    return when (T::class) {
        String::class -> getStringDirect(key, default as String) as T
        Int::class -> getIntDirect(key, default as Int) as T
        Boolean::class -> getBooleanDirect(key, default as Boolean) as T
        Float::class -> getFloatDirect(key, default as Float) as T
        Long::class -> getLongDirect(key, default as Long) as T
        else -> {
            getStringDirectOrNull(key)?.let {
                try {
                    localStorageJson.decodeFromString<T>(it)
                } catch (e: Exception) {
                    localStorageExtLogger.e(e) { "Failed to decode key (Direct): $key for type: ${T::class.simpleName}" }
                    default
                }
            } ?: default
        }
    }
}

inline fun <reified T : Any> LocalStorage.getDirectOrNull(key: String): T? {
    return when (T::class) {
        String::class -> getStringDirectOrNull(key) as T?
        Int::class -> getIntDirectOrNull(key) as T?
        Float::class -> getFloatDirectOrNull(key) as T?
        Long::class -> getLongDirectOrNull(key) as T?
        else -> {
            getStringDirectOrNull(key)?.let {
                try {
                    localStorageJson.decodeFromString<T>(it)
                } catch (e: Exception) {
                    localStorageExtLogger.e(e) { "Failed to decode key (Direct): $key for type: ${T::class.simpleName}" }
                    null
                }
            }
        }
    }
}

/**
 * Adds Property Delegate support for LocalStorage.
 * Usage: var authInfo by storage.ksafe(AuthInfo())
 */
inline fun <reified T : Any> LocalStorage.ksafe(
    default: T,
    key: String? = null
): ReadWriteProperty<Any?, T> = object : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        // Use synchronous method to read, default to property name if key is not provided
        return getDirect(key ?: property.name, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        // Use synchronous method to write
        saveDirect(key ?: property.name, value)
    }
}

expect fun LocalStorage.saveFirebaseAuth(user: User?)
expect fun LocalStorage.getFireBaseAuth(): User?
