package io.lackstudio.omnifeed.auth.platform

import io.lackstudio.omnifeed.auth.domain.model.User

expect fun initializeFirebase(preferencesPathName: String? = null, firebaseConfig: String? = null)

expect val firebaseApiKey: String?

expect fun saveAuthUser(user: User?)
expect fun loadAuthUser(): User?
