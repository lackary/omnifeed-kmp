package io.lackstudio.omnifeed.auth.data.storage

import io.lackstudio.omnifeed.auth.domain.model.User

actual fun LocalStorage.saveFirebaseAuth(user: User?) {
    runCatching {
        saveDirect(FIREBASE_AUTH_USER_KEY, user)
    }.onFailure { e ->
        localStorageExtLogger.e(e) { "Failed to save auth user" }
    }
}

actual fun LocalStorage.getFireBaseAuth(): User? = getDirectOrNull(FIREBASE_AUTH_USER_KEY)
