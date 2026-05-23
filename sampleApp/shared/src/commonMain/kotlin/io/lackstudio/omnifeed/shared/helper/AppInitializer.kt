package io.lackstudio.omnifeed.shared.helper

import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import io.lackstudio.omnifeed.shared.config.BuildKonfig

object AppInitializer {
    fun onApplicationStart(serverId: String? = null) {

        GoogleAuthProvider.create(
            credentials = GoogleAuthCredentials(
                serverId = serverId?: BuildKonfig.GOOGLE_SERVICES_WEB_CLIENT_ID
            )
        )
    }
}
