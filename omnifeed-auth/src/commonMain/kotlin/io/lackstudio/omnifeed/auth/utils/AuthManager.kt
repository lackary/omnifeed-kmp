package io.lackstudio.omnifeed.auth.utils

data class GoogleAuthTokens(
    val idToken: String,
    val accessToken: String? = null
)

interface AuthManager {
    fun setRedirectUrl(url: String)
    fun setClientId(id: String)
    fun setSuccessHtml(html: String)
    
    fun getRedirectUrl(): String
    fun startLogin(authUrl: String)
    suspend fun signInWithGoogle(context: Any? = null): GoogleAuthTokens?
    suspend fun signInWithOAuthPopup(authUrl: String): String?
    suspend fun signOut()
}
