package io.lackstudio.omnifeed.auth.data.error

import io.lackstudio.omnifeed.auth.data.remote.model.response.AuthErrorResponse
import io.lackstudio.omnifeed.core.network.error.RemoteException
import io.lackstudio.omnifeed.core.network.error.StructuredApiException

class AuthApiException(
    val apiError: AuthErrorResponse? = null,
    override val originalApiException: RemoteException.Api
) : Exception(originalApiException.message, originalApiException), StructuredApiException {

    override val structuredMessage: String?
        get() = apiError?.error?.message ?: originalApiException.message
}
