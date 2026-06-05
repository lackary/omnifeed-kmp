package io.lackstudio.omnifeed.auth.platform

import android.app.Application
import co.touchlab.kermit.Logger
import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import io.lackstudio.omnifeed.auth.utils.GoogleServiceWeb
import io.lackstudio.omnifeed.core.utils.base64ToJson
import java.util.prefs.BackingStoreException
import java.util.prefs.Preferences


@PublishedApi
internal val logger = Logger.withTag("FirebaseUtils")

/**
 * Global holder for Firebase API Key on JVM for REST fallback.
 */
private var _firebaseApiKey: String? = null
actual val firebaseApiKey: String? get() = _firebaseApiKey

/**
 *  Initializes the Firebase SDK for the JVM platform using the provided configuration.
 *
 * This function sets up the [FirebasePlatform] using the Java Preferences API for persistent storage
 * and initializes the Firebase SDK with the provided configuration.
 *
 * @param preferencesPathName The path name used for [java.util.prefs.Preferences] to store Firebase data.
 * @param firebaseConfig A Base64 encoded JSON string containing the Firebase configuration (GoogleServiceWeb).
 */
actual fun initializeFirebase(preferencesPathName: String?, firebaseConfig: String?) {
    if (firebaseConfig == null) {
        logger.e { "firebaseConfig must have a value" }
        return
    }
    // Desktop initialization can be added here if needed
    FirebasePlatform.initializeFirebasePlatform(
        object : FirebasePlatform() {
            private val prefs = Preferences.userRoot().node(preferencesPathName)

            override fun store(key: String, value: String) {
                prefs.put(key, value)
                forceSyncToDisk()
            }

            override fun retrieve(key: String): String? = prefs.get(key, null)

            override fun clear(key: String) {
                prefs.remove(key)
                forceSyncToDisk()
            }

            override fun log(msg: String) = logger.d { "Firebase JVM: $msg" }

            private fun forceSyncToDisk() {
                try {
                    prefs.flush()
                } catch (e: BackingStoreException) {
                    logger.e(throwable = e) { "Failed to flush preferences to disk" }
                }
            }
        }
    )

    val config = base64ToJson<GoogleServiceWeb>(firebaseConfig)
    if (config != null) {
        _firebaseApiKey = config.apiKey
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
