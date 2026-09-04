package io.lackstudio.omnifeed.auth.platform

import android.app.Application
import co.touchlab.kermit.Logger
import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import io.lackstudio.omnifeed.auth.data.storage.LocalStorage
import io.lackstudio.omnifeed.auth.utils.GoogleServiceWeb
import io.lackstudio.omnifeed.core.utils.base64ToJson

@PublishedApi
internal val logger = Logger.withTag("FirebaseUtils")

/**
 * Global holder for Firebase API Key on JVM for REST fallback.
 */
private var _firebaseApiKey: String? = null
actual val firebaseApiKey: String? get() = _firebaseApiKey

private var _firebaseProjectId: String? = null
actual val firebaseProjectId: String? get() = _firebaseProjectId

actual val isJvm: Boolean = true

private var _localStorage: LocalStorage? = null

/**
 *  Initializes the Firebase SDK for the JVM platform using the provided configuration.
 *
 * This function sets up the [FirebasePlatform] using KSafe for persistent storage
 * and initializes the Firebase SDK with the provided configuration.
 *
 * @param [firebaseConfig] A Base64 encoded JSON string containing the Firebase configuration (GoogleServiceWeb).
 * @param [localStorage] The localsStorage was used for KSafe / the others storage to store Firebase data.
 */
actual fun initializeFirebase(firebaseConfig: String?, localStorage: LocalStorage?) {
    _localStorage = localStorage
    if (firebaseConfig == null) {
        logger.e { "firebaseConfig must have a value" }
        return
    }

    if (localStorage == null) {
        logger.e { "localStorage must not have a null" }
        return
    }
    
    FirebasePlatform.initializeFirebasePlatform(
        object : FirebasePlatform() {
            override fun store(key: String, value: String) {
                localStorage.saveStringDirect(key, value)
            }

            override fun retrieve(key: String): String? {
                return try {
                    localStorage.getStringDirectOrNull(key)
                } catch (e: Exception) {
                    null
                }
            }
            override fun clear(key: String) {
                localStorage.deleteDirect(key)
            }

            override fun log(msg: String) = logger.d { "Firebase JVM: $msg" }
        }
    )

    val config = base64ToJson<GoogleServiceWeb>(firebaseConfig)
    if (config != null) {
        _firebaseApiKey = config.apiKey
        _firebaseProjectId = config.projectId
        Firebase.initialize(
            context = Application(),
            options = FirebaseOptions(
                apiKey = config.apiKey,
                authDomain = config.authDomain,
                projectId = config.projectId,
                storageBucket = config.storageBucket,
                applicationId = config.appId
            )
        )
        logger.i { "Firebase JVM initialized successfully" }
    } else {
        logger.w { "Firebase JVM config not found" }
    }
}
