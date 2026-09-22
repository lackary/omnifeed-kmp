package io.lackstudio.omnifeed.auth.utils

import co.touchlab.kermit.Logger
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import kotlin.coroutines.resume

interface GoogleSignInProvider {
    fun signIn(completion: (GoogleAuthTokens?) -> Unit)
    fun signOut() {}
}

class IosAuthManager : AuthManager {

    companion object {
        private var googleSignInProvider: GoogleSignInProvider? = null

        fun setGoogleSignInProvider(provider: GoogleSignInProvider?) {
            googleSignInProvider = provider
        }

        fun getGoogleSignInProvider(): GoogleSignInProvider? = googleSignInProvider
    }

    private var _redirectUrl: String? = null
    private var _clientId: String? = null
    private var _successHtml: String? = null

    override fun setRedirectUrl(url: String) { _redirectUrl = url }
    override fun setClientId(id: String) { _clientId = id }
    override fun setSuccessHtml(html: String) { _successHtml = html }

    private val logger = Logger.withTag("IosAuthManager")

    override fun getRedirectUrl(): String {
        return _redirectUrl ?: "omnihub://auth/callback"
    }

    override fun startLogin(authUrl: String) {
        val nsUrl = NSURL.URLWithString(authUrl) ?: return

        if (UIApplication.sharedApplication.canOpenURL(nsUrl)) {
            UIApplication.sharedApplication.openURL(
                nsUrl,
                mapOf<Any?, Any>(),
                null
            )
        }
    }

    override suspend fun signInWithOAuthPopup(authUrl: String): String? {
        throw UnsupportedOperationException("OAuth popup is only supported on Web")
    }

    override suspend fun signInWithGoogle(context: Any?): GoogleAuthTokens? {
        val provider = googleSignInProvider
        if (provider == null) {
            logger.w { "googleSignInProvider is not configured in IosAuthManager" }
            return null
        }
        return suspendCancellableCoroutine { continuation ->
            provider.signIn { tokens ->
                if (continuation.isActive) {
                    continuation.resume(tokens)
                }
            }
        }
    }

    override suspend fun signOut() {
        logger.d { "iOS Google Sign-Out executed" }
        googleSignInProvider?.signOut()
    }
}
