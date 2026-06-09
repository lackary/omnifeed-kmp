package io.lackstudio.omnifeed.auth

import web.dom.document
import web.dom.ElementId
import web.html.HTMLScriptElement

internal object WebAuthBridge {
    fun injectIfNeeded() {
        val scriptId = "google-auth-bridge-script"
        val elementId = ElementId(scriptId)
        if (document.getElementById(elementId) != null) return

        val scriptContent = """
            window.initAndPromptGoogleSignIn = function(clientId, callback) {
                console.log("JS Bridge (Injected): Initializing Google Auth with Client ID: " + clientId);
                if (typeof google === 'undefined' || !google.accounts || !google.accounts.id) {
                    console.error("JS Bridge: Google Identity Services SDK not loaded yet.");
                    callback(null)
                    return;
                }

                google.accounts.id.initialize({
                    client_id: clientId,
                    callback: (response) => {
                        console.log("JS Bridge: Received response from Google");
                        callback(response.credential);
                    }
                });
                google.accounts.id.prompt((notification) => {
                    if (notification.isNotDisplayed()) {
                        console.warn("JS Bridge: Prompt not displayed:", notification.getNotDisplayedReason());
                        callback(null);
                    } else if (notification.isSkippedMoment()) {
                        console.warn("JS Bridge: Prompt skipped:", notification.getSkippedReason());
                        callback(null);
                    } else if (notification.isDismissedMoment()) {
                        console.warn("JS Bridge: Prompt dismissed:", notification.getDismissedReason());
                        callback(null);
                    }
                });
            };
        """.trimIndent()

        val scriptElement = document.createElement("script") as HTMLScriptElement
        scriptElement.id = elementId
        scriptElement.textContent = scriptContent
        document.head.appendChild(scriptElement)
    }
}
