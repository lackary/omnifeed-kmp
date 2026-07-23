package io.lackstudio.omnifeed.auth.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.core.net.toUri
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import co.touchlab.kermit.Logger
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.util.UUID

class AndroidAuthManager(private val context: Context) : AuthManager {

    private var _redirectUrl: String? = null
    private var _clientId: String? = null
    private var _successHtml: String? = null

    override fun setRedirectUrl(url: String) { _redirectUrl = url }
    override fun setClientId(id: String) { _clientId = id }
    override fun setSuccessHtml(html: String) { _successHtml = html }

    private val logger = Logger.withTag("AndroidAuthManager")
    private val credentialManager = CredentialManager.create(context)

    override fun getRedirectUrl(): String {
        return _redirectUrl ?: ""
    }

    override fun startLogin(authUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, authUrl.toUri())
        @SuppressLint("WrongConstant")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override suspend fun signInWithOAuthPopup(authUrl: String): String? {
        throw UnsupportedOperationException("OAuth popup is only supported on Web")
    }

    override suspend fun signInWithGoogle(context: Any?): GoogleAuthTokens? {
        val activityContext = (context as? Context)?.findActivity() ?: return null

        val nonce = UUID.randomUUID().toString()

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(_clientId ?: "")
            .setNonce(nonce)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            logger.i { "Launching Credential Manager..." }
            val result = credentialManager.getCredential(
                context = activityContext,
                request = request
            )
            val credential = result.credential

            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                logger.i { "Successfully parsed GoogleIdTokenCredential" }
                GoogleAuthTokens(idToken = googleIdTokenCredential.idToken)
            } catch (e: Exception) {
                logger.e(throwable = e) { "Failed to parse GoogleIdTokenCredential: ${e.message}" }
                null
            }
        } catch (e: Exception) {
            logger.e(throwable = e) { "Error during getCredential: ${e.message}" }
            null
        }
    }

    override suspend fun signOut() {
        // No additional cleanup needed for Credential Manager on Android for now
    }

    private fun Context.findActivity(): Activity? {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }
}
