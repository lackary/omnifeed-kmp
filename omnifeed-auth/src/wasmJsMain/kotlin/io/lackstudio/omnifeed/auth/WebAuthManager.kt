package io.lackstudio.omnifeed.auth

import co.touchlab.kermit.Logger
import kotlinx.browser.window
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsString

class WebAuthManager : AuthManager {

    private var _redirectUrl: String? = null
    private var _clientId: String? = null
    private var _successHtml: String? = null

    override fun setRedirectUrl(url: String) { _redirectUrl = url }
    override fun setClientId(id: String) { _clientId = id }
    override fun setSuccessHtml(html: String) { _successHtml = html }

    private val logger = Logger.withTag("WebAuthManager")

    override fun getRedirectUrl(): String {
        if (_redirectUrl != null) return _redirectUrl!!
        val origin = window.location.protocol + "//" + window.location.host + window.location.pathname
        return origin.removeSuffix("/")
    }

    override fun startLogin(authUrl: String) {
        logger.d { "Web Redirecting to: $authUrl" }
        window.location.href = authUrl
    }

    @OptIn(ExperimentalWasmJsInterop::class)
    override suspend fun signInWithGoogle(context: Any?): GoogleAuthTokens? = suspendCancellableCoroutine { continuation ->
        try {
            initAndPromptGoogleSignIn(
                clientId = _clientId ?: ""
            ) { credential ->
                if (credential != null) {
                    logger.d { "Google Sign-In: Received credential" }
                    continuation.resume(GoogleAuthTokens(idToken = credential.toString()))
                } else {
                    logger.w { "Google Sign-In: Credential is null" }
                    continuation.resume(null)
                }
            }
        } catch (e: Exception) {
            logger.e(throwable = e) { "Google Sign-In: Error during initialization" }
            continuation.resume(null)
        }
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(clientId, callback) => window.initAndPromptGoogleSignIn(clientId, callback)")
private external fun initAndPromptGoogleSignIn(clientId: String, callback: (JsString?) -> Unit)
