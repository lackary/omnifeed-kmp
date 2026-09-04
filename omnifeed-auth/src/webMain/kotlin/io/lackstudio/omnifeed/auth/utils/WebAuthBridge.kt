package io.lackstudio.omnifeed.auth.utils

import io.lackstudio.omnifeed.auth.GoogleAuthBridgeJs
import web.dom.ElementId
import web.dom.document
import web.html.HTMLScriptElement

internal object WebAuthBridge {
    fun injectIfNeeded() {
        val scriptId = "google-auth-bridge-script"
        val elementId = ElementId(scriptId)
        if (document.getElementById(elementId) != null) return

        val scriptElement = document.createElement("script") as HTMLScriptElement
        scriptElement.id = elementId
        scriptElement.textContent = GoogleAuthBridgeJs.CONTENT
        document.head.appendChild(scriptElement)
    }
}
