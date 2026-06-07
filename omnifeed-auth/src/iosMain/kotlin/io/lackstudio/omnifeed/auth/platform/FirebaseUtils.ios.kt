package io.lackstudio.omnifeed.auth.platform

import io.lackstudio.omnifeed.auth.domain.model.User

actual fun initializeFirebase(preferencesPathName: String?, firebaseConfig: String?) {
}

actual val firebaseApiKey: String? = null

actual fun saveAuthUser(user: User?) {}
actual fun loadAuthUser(): User? = null
