package io.lackstudio.omnifeed.auth.platform

import com.google.firebase.FirebaseApp
import io.lackstudio.omnifeed.auth.data.storage.LocalStorage
import io.lackstudio.omnifeed.auth.utils.GoogleServiceWeb
import io.lackstudio.omnifeed.core.utils.base64ToJson

private var _firebaseApiKey: String? = null
private var _firebaseProjectId: String? = null

actual fun initializeFirebase(firebaseConfig: String?, localStorage: LocalStorage?) {
    if (firebaseConfig != null) {
        val config = base64ToJson<GoogleServiceWeb>(firebaseConfig)
        if (config != null) {
            _firebaseApiKey = config.apiKey
            _firebaseProjectId = config.projectId
        }
    }
}

actual val firebaseApiKey: String?
    get() = _firebaseApiKey ?: try {
        FirebaseApp.getInstance().options.apiKey
    } catch (e: Exception) {
        null
    }

actual val firebaseProjectId: String?
    get() = _firebaseProjectId ?: try {
        FirebaseApp.getInstance().options.projectId
    } catch (e: Exception) {
        null
    }

actual val isJvm: Boolean = false
