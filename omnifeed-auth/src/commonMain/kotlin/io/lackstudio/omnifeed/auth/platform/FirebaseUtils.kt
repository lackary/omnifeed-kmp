package io.lackstudio.omnifeed.auth.platform

import io.lackstudio.omnifeed.auth.data.storage.LocalStorage
import io.lackstudio.omnifeed.auth.domain.model.User

expect fun initializeFirebase(firebaseConfig: String? = null, localStorage: LocalStorage? = null)

expect val firebaseApiKey: String?
expect val firebaseProjectId: String?

expect val isJvm: Boolean
