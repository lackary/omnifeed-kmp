package io.lackstudio.omnifeed.auth.utils

import co.touchlab.kermit.Logger
import kotlinx.browser.window
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

class WebAuthManager : AuthManager {

    private var _redirectUrl: String? = null
    private var _clientId: String? = null
    private var _successHtml: String? = null

    override fun setRedirectUrl(url: String) { _redirectUrl = url }
    override fun setClientId(id: String) { _clientId = id }
    override fun setSuccessHtml(html: String) { _successHtml = html }

    private val logger = Logger.withTag("WebAuthManager")
    private var currentContinuation: CancellableContinuation<GoogleAuthTokens?>? = null

    override fun getRedirectUrl(): String {
        if (_redirectUrl != null) return _redirectUrl!!
        val origin = window.location.protocol + "//" + window.location.host + window.location.pathname
        return origin.removeSuffix("/")
    }

    override fun startLogin(authUrl: String) {
        logger.d { "Web Redirecting to: $authUrl" }
        window.location.href = authUrl
    }

    override suspend fun signInWithOAuthPopup(authUrl: String): String? {
        logger.d { "Starting OAuth popup for: $authUrl" }

        return suspendCancellableCoroutine { continuation ->
            val popupWidth = 600
            val popupHeight = 700
            val left = (window.screen.width - popupWidth) / 2
            val top = (window.screen.height - popupHeight) / 2

            val popupOptions = "width=$popupWidth,height=$popupHeight,top=$top,left=$left,status=no,resizable=yes,scrollbars=yes"
            val popup = window.open(authUrl, "oauth-popup", popupOptions)

            if (popup == null) {
                logger.e { "Popup blocked or failed to open" }
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            var intervalId: Int = -1
            var listenerReference: ((dynamic) -> Unit)? = null

            val cleanup = {
                if (intervalId != -1) window.clearInterval(intervalId)
                listenerReference?.let { window.removeEventListener("message", it) }
            }

            val messageListener: (dynamic) -> Unit = { event ->
                val data = event.data
                if (data != null) {
                    logger.d { "Received message from popup. Origin: ${event.origin}, Data type: ${js("typeof data")}" }

                    // Check for our specific auth_success message
                    // We use js() check here to be absolutely safe with dynamic types across JS/Wasm
                    val isAuthSuccess = js("data && data.type === 'auth_success'") as Boolean

                    if (isAuthSuccess) {
                        val code = data.code as? String
                        if (continuation.isActive) {
                            logger.d { "Received code from popup: $code" }
                            cleanup()
                            popup.close()
                            continuation.resume(code)
                        }
                    }
                }
            }
            listenerReference = messageListener

            window.addEventListener("message", messageListener)

            intervalId = window.setInterval({
                if (popup.closed == true) {
                    cleanup()
                    if (continuation.isActive) {
                        logger.d { "Popup closed by user" }
                        continuation.resume(null)
                    }
                }
            }, 500)

            continuation.invokeOnCancellation {
                cleanup()
                if (!popup.closed) popup.close()
            }
        }
    }

    override suspend fun signInWithGoogle(context: Any?): GoogleAuthTokens? {
        // Inject the JS bridge if it's not already there
        WebAuthBridge.injectIfNeeded()

        // Ensure any previous ongoing login process is cancelled to avoid overlapping states
        currentContinuation?.let {
            if (it.isActive) {
                logger.d { "Cancelling previous Google Sign-In attempt" }
                it.resume(null)
            }
        }
        currentContinuation = null

        // IMPORTANT: Clear Google's g_state cookie.
        // When a user manually closes the Google sign-in window, Google sets this cookie and enters a suppression period,
        // causing subsequent prompt() calls to have no response. Clearing it ensures the prompt can be re-triggered on every click.
        clearGoogleStateCookie()

        return try {
            withTimeout(30000.milliseconds) {
                suspendCancellableCoroutine { continuation ->
                    currentContinuation = continuation
                    try {
                        val clientId = _clientId
                        if (clientId.isNullOrBlank()) {
                            logger.e { "Google Client ID is missing. Please call setClientId() first." }
                            continuation.resume(null)
                            currentContinuation = null
                            return@suspendCancellableCoroutine
                        }

                        initAndPromptGoogleSignIn(clientId) { credential ->
                            // Ensure response is only handled for the current continuation
                            if (currentContinuation == continuation) {
                                if (credential != null) {
                                    logger.d { "Google Sign-In: Received credential successfully" }
                                    continuation.resume(GoogleAuthTokens(idToken = credential))
                                } else {
                                    logger.w { "Google Sign-In: Credential is null (dismissed or suppressed)" }
                                    continuation.resume(null)
                                }
                                currentContinuation = null
                            }
                        }
                    } catch (e: Exception) {
                        logger.e(throwable = e) { "Google Sign-In: Error during JS initialization" }
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                        currentContinuation = null
                    }

                    continuation.invokeOnCancellation {
                        if (currentContinuation == continuation) {
                            currentContinuation = null
                        }
                    }
                }
            }
        } catch (e: Exception) {
            if (e is TimeoutCancellationException) {
                logger.e { "Google Sign-In: Timed out after 30s waiting for user action." }
            } else {
                logger.e(throwable = e) { "Google Sign-In: Unexpected error." }
            }
            currentContinuation = null
            null
        }
    }

    override suspend fun signOut() {
        logger.d { "signOut: Clearing all cookies and storage" }
        try {
            // 1. Clear Google state cookie specifically
            clearGoogleStateCookie()

            // 2. Clear all cookies for the current domain
            val document = window.document
            val cookies = document.cookie.split(";")
            for (cookie in cookies) {
                val name = cookie.split("=").firstOrNull()?.trim()
                if (!name.isNullOrEmpty()) {
                    document.cookie = "$name=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/;"
                }
            }

            // 3. Clear Storage
            window.localStorage.clear()
            window.sessionStorage.clear()
            
        } catch (e: Exception) {
            logger.w { "Error during WebAuthManager signOut: ${e.message}" }
        }
    }

    private fun clearGoogleStateCookie() {
        try {
            // Google Identity Services sets the g_state cookie after a user dismisses the prompt.
            // Clearing it is the most effective Kotlin-side fix for the "no response on click" issue.
            val document = window.document
            document.cookie = "g_state=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;"
            logger.d { "Google g_state cookie cleared successfully" }
        } catch (e: Exception) {
            logger.w { "Failed to clear g_state cookie: ${e.message}" }
        }
    }
}

private fun initAndPromptGoogleSignIn(clientId: String, callback: (String?) -> Unit) {
    // In JS we can use js() block or just call window
    val dynamicWindow: dynamic = window
    dynamicWindow.initAndPromptGoogleSignIn(clientId, callback)
}
