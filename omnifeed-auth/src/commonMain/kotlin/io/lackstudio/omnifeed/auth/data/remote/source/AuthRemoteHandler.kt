package io.lackstudio.omnifeed.auth.data.remote.source

import co.touchlab.kermit.Logger
import io.lackstudio.omnifeed.auth.data.error.AuthApiException
import io.lackstudio.omnifeed.auth.data.remote.model.response.AuthErrorResponse
import io.lackstudio.omnifeed.core.network.remote.toResult
import io.lackstudio.omnifeed.core.network.error.RemoteException
import kotlinx.serialization.json.Json

@PublishedApi
internal val logger = Logger.withTag("AuthRemoteHandler")

/**
 * Executes a network call and handles Auth-specific errors by throwing [AuthApiException].
 * This follows the "Throwing" style for cleaner internal data flow.
 */
suspend inline fun <T> handleAuthApi(name: String = "AuthApi", call: suspend () -> T): T {
    val result = toResult(name = name, call = call)
    
    return result.getOrElse { exception ->
        if (exception is RemoteException.Api) {
            exception.errorBody?.takeIf { it.isNotBlank() }?.let { bodyString ->
                try {
                    val json = Json { ignoreUnknownKeys = true }
                    val errorResponse = json.decodeFromString<AuthErrorResponse>(bodyString)
                    throw AuthApiException(
                        apiError = errorResponse,
                        originalApiException = exception
                    )
                } catch (e: Exception) {
                    if (e is AuthApiException) throw e
                    logger.e(e) { "Failed to parse Auth error body" }
                }
            }
        }
        throw exception
    }
}
