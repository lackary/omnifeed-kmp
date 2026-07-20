package io.lackstudio.omnifeed.core.common.error

import io.lackstudio.omnifeed.core.network.error.RemoteException
import io.lackstudio.omnifeed.core.network.error.StructuredApiException

actual fun Throwable.getFriendlyMessage(): String {
    val msg = when (this) {
        is StructuredApiException -> structuredMessage ?: originalApiException.errorBody ?: message ?: ""
        is RemoteException.Api -> errorBody ?: message ?: ""
        else -> message ?: ""
    }
    
    if (msg.isBlank()) return "An unknown error occurred"

    // On iOS, native NSError descriptions are often wrapped in double quotes.
    // Example: Error Domain=FIRAuthErrorDomain Code=17007 "The email address is already in use by another account."
    val regex = Regex("\"([^\"]*)\"")
    val match = regex.find(msg)
    val friendly = match?.groupValues?.get(1) ?: msg
    
    return when {
        friendly.contains("CREDENTIAL_TOO_OLD_LOGIN_AGAIN") -> "For security reasons, please log in again to verify your identity, then try setting your password again."
        else -> friendly
    }
}
