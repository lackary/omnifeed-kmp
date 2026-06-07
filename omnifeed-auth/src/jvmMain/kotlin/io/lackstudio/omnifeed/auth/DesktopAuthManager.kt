package io.lackstudio.omnifeed.auth

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CompletableDeferred
import java.awt.Desktop
import java.awt.Taskbar
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.URI
import java.net.URLEncoder
import java.util.UUID
import kotlin.concurrent.thread

class DesktopAuthManager : AuthManager {

    private var _redirectUrl: String? = null
    private var _clientId: String? = null
    private var _successHtml: String? = null

    override fun setRedirectUrl(url: String) { _redirectUrl = url }
    override fun setClientId(id: String) { _clientId = id }
    override fun setSuccessHtml(html: String) { _successHtml = html }

    private val logger = Logger.withTag("DesktopAuthManager")
    private val callbackPort = 54321
    private var resultDeferred: CompletableDeferred<GoogleAuthTokens?>? = null

    override fun getRedirectUrl(): String {
        return _redirectUrl ?: "http://localhost:$callbackPort/callback"
    }

    override fun startLogin(authUrl: String) {
        // Start a thread to run the Server (to avoid blocking the UI)
        thread {
            try {
                // Simple HTTP Server
                ServerSocket(callbackPort).use { serverSocket ->
                    logger.d { "Desktop Auth Server listening on port $callbackPort..." }

                    // Open the system browser and go to the OAuth2 login page
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(URI(authUrl))
                    }

                    var authenticated = false
                    // Loop to handle potential redirects (e.g., fragment to query for Google)
                    while (!authenticated) {
                        // Wait for browser redirect (Blocking)
                        serverSocket.accept().use { clientSocket ->
                            // Read Request
                            val reader = clientSocket.getInputStream().bufferedReader()
                            val requestLine = reader.readLine() // e.g., "GET /callback?code=XYZ... HTTP/1.1"

                            if (requestLine != null) {
                                if (requestLine.contains("code=")) {
                                    val code = requestLine.substringAfter("code=").substringBefore("&").substringBefore(" ")
                                    logger.i { "Desktop received Auth Code (masked): ${code.take(4)}***" }

                                    // Push into DeepLinkBuffer
                                    DeepLinkBuffer.setDeepLink("omnihub://auth/callback?code=$code")
                                    authenticated = true
                                } else if (requestLine.contains("id_token=")) {
                                    val idToken = requestLine.substringAfter("id_token=").substringBefore("&").substringBefore(" ")
                                    logger.i { "Desktop received Google ID Token" }

                                    val tokens = GoogleAuthTokens(idToken = idToken)
                                    resultDeferred?.complete(tokens)

                                    // Push into DeepLinkBuffer
                                    DeepLinkBuffer.setDeepLink("omnihub://auth/callback?idToken=$idToken")
                                    authenticated = true
                                }
                            }

                            // Return HTML to the browser
                            val writer = PrintWriter(clientSocket.getOutputStream())
                            writer.println("HTTP/1.1 200 OK")
                            writer.println("Content-Type: text/html; charset=UTF-8")
                            writer.println("\r\n")

                            if (authenticated) {
                                writer.print(_successHtml ?: "<html><body><h1>Login Successful</h1></body></html>")
                            } else {
                                writer.print("<html><script>if(window.location.hash){window.location.search=window.location.hash.substring(1);}else{document.body.innerHTML='<h1>Login Failed</h1><p>No authorization code or token found.</p>';}</script><body><p>Processing login...</p></body></html>")
                            }
                            writer.flush()
                        }
                    }
                }

                try {
                    if (Taskbar.isTaskbarSupported() &&
                        Taskbar.getTaskbar().isSupported(Taskbar.Feature.USER_ATTENTION)) {
                        Taskbar.getTaskbar().requestUserAttention(true, true)
                    }
                } catch (e: Exception) {
                    logger.e { "Exception error message: ${e.message}" }
                }

            } catch (e: Exception) {
                logger.e(throwable = e) { "Error in DesktopAuthManager" }
            }
        }
    }

    override suspend fun signInWithGoogle(context: Any?): GoogleAuthTokens? {
        val deferred = CompletableDeferred<GoogleAuthTokens?>()
        resultDeferred = deferred

        val scope = "email profile openid"
        val encodedRedirect = URLEncoder.encode(getRedirectUrl(), "UTF-8")
        val encodedScope = URLEncoder.encode(scope, "UTF-8")
        val nonce = UUID.randomUUID().toString()

        val authUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=${_clientId ?: ""}&" +
                "redirect_uri=$encodedRedirect&" +
                "response_type=id_token&" +
                "scope=$encodedScope&" +
                "nonce=$nonce&" +
                "prompt=select_account"

        startLogin(authUrl)

        return resultDeferred?.await()
    }
}
