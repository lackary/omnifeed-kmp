package io.lackstudio.omnifeed.auth.platform

import co.touchlab.kermit.Logger
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import io.lackstudio.omnifeed.auth.data.storage.LocalStorage
import io.lackstudio.omnifeed.auth.utils.GoogleServiceWeb
import io.lackstudio.omnifeed.core.utils.base64ToJson

@PublishedApi
internal val logger = Logger.withTag("FirebaseUtils")

private var _firebaseApiKey: String? = null

/**
 * Initializes the Firebase SDK for the JS platform using the provided configuration.
 *
 * This function decodes a Base64-encoded JSON string into a [GoogleServiceWeb] object
 * to configure the Firebase options including API key, project ID, and application ID.
 *
 * @param [firebaseConfig] A Base64-encoded JSON string containing the Firebase web configuration.
 * @param [localStorage] This parameter is not utilized in the JS implementation.
 * If null or invalid, initialization is skipped.
 */
actual fun initializeFirebase(firebaseConfig: String?, localStorage: LocalStorage?) {
    if (firebaseConfig == null) {
        return
    }
    val config = base64ToJson<GoogleServiceWeb>(firebaseConfig)
    if (config != null) {
        _firebaseApiKey = config.apiKey
        Firebase.initialize(
            options = FirebaseOptions(
                apiKey = config.apiKey,
                authDomain = config.authDomain,
                projectId = config.projectId,
                storageBucket = config.storageBucket,
                applicationId = config.appId
            )
        )
        logger.d {"Firebase JS initialized successfully"}
    } else {
        logger.w { "Firebase JS config not found" }
    }
}

actual val firebaseApiKey: String? get() = _firebaseApiKey
