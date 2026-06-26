package io.lackstudio.omnifeed.auth.data.storage

import io.lackstudio.omnifeed.auth.domain.model.User

actual fun LocalStorage.saveFirebaseAuth(user: User?) {}

actual fun LocalStorage.getFireBaseAuth(): User? = null
