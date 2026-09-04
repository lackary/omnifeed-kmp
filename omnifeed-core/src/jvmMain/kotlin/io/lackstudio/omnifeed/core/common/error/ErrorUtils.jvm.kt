package io.lackstudio.omnifeed.core.common.error

import io.lackstudio.omnifeed.core.network.error.RemoteException
import io.lackstudio.omnifeed.core.network.error.StructuredApiException

actual fun Throwable.getFriendlyMessage(): String {
    // If it's our structured API exception, use its message first
    val msg = when (this) {
        is StructuredApiException -> structuredMessage ?: originalApiException.errorBody ?: message ?: ""
        is RemoteException.Api -> errorBody ?: message ?: ""
        else -> message ?: ""
    }

    return when {
        msg.contains("CREDENTIAL_TOO_OLD_LOGIN_AGAIN") -> "For security reasons, please log in again to verify your identity, then try setting your password again."
        msg.contains("TOKEN_EXPIRED") -> "Login session has expired, please log in again."
        msg.contains("USER_NOT_FOUND") -> "User not found."
        msg.contains("INVALID_PASSWORD") || msg.contains("INVALID_LOGIN_CREDENTIALS") || msg.contains("INVALID_CREDENTIAL") -> "Invalid email or password."
        msg.contains("EMAIL_NOT_FOUND") -> "Email not found."
        msg.contains("USER_DISABLED") -> "This account has been disabled."
        msg.contains("EMAIL_EXISTS") -> "This email is already in use."
        msg.contains("WEAK_PASSWORD") -> "The password is too weak."
        msg.contains("TOO_MANY_ATTEMPTS_TRY_LATER") -> "Too many failed attempts. Please try again later."
        msg.contains("INVALID_EMAIL") -> "The email address is invalid."
        msg.contains("USER_MISMATCH") -> "The credentials provided do not match the logged-in user."
        msg.contains("REQUIRES_RECENT_LOGIN") -> "For security reasons, please log out and log in again before performing this action."
        else -> {
            // If it's a raw Firebase SDK error with JSON, try to extract just the message part
            if (msg.contains("\"message\":")) {
                val extracted = msg.substringAfter("\"message\": \"").substringBefore("\"")
                if (extracted.isNotBlank() && extracted != msg) {
                    // Try to make common technical messages a bit nicer
                    when (extracted) {
                        "INVALID_LOGIN_CREDENTIALS" -> "Invalid email or password."
                        "CREDENTIAL_TOO_OLD_LOGIN_AGAIN" -> "Login session expired. Please log in again."
                        else -> extracted
                    }
                } else {
                    msg.takeIf { it.isNotBlank() } ?: "An unknown error occurred."
                }
            } else {
                msg.takeIf { it.isNotBlank() } ?: "An unknown error occurred, please try again later."
            }
        }
    }
}
