package io.lackstudio.omnifeed.core.common.error

import io.lackstudio.omnifeed.core.network.error.RemoteException
import io.lackstudio.omnifeed.core.network.error.StructuredApiException

actual fun Throwable.getFriendlyMessage(): String {
    val msg = when (this) {
        is StructuredApiException -> structuredMessage ?: originalApiException.errorBody ?: message ?: ""
        is RemoteException.Api -> errorBody ?: message ?: ""
        else -> message ?: ""
    }
    return msg.takeIf { it.isNotBlank() } ?: "An unknown error occurred"
}
