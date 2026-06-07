package io.lackstudio.omnifeed.auth

import co.touchlab.kermit.Logger
import cocoapods.GoogleSignIn.GIDSignIn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import kotlin.coroutines.resume

class IosAuthManager : AuthManager {

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

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun signInWithGoogle(context: Any?): GoogleAuthTokens? =
        suspendCancellableCoroutine { continuation ->
            val rootViewController = getRootViewController()
            if (rootViewController == null) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            GIDSignIn.sharedInstance.signInWithPresentingViewController(rootViewController) { result, error ->
                if (error != null) {
                    logger.d { "iOS Google Sign-In Error: ${error.localizedDescription}" }
                    continuation.resume(null)
                } else {
                    val idToken = result?.user?.idToken?.tokenString
                    val accessToken = result?.user?.accessToken?.tokenString

                    if (idToken != null) {
                        continuation.resume(GoogleAuthTokens(idToken, accessToken))
                    } else {
                        continuation.resume(null)
                    }
                }
            }
        }

    private fun getRootViewController(): UIViewController? {
        val keyWindow = UIApplication.sharedApplication.windows.asSequence()
            .mapNotNull { it as? UIWindow }
            .firstOrNull { it.isKeyWindow() }
        return keyWindow?.rootViewController ?: UIApplication.sharedApplication.keyWindow?.rootViewController
    }
}
