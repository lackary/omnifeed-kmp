package io.lackstudio.omnifeed.core.network.remote

import co.touchlab.kermit.Logger
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.JsonConvertException
import io.lackstudio.omnifeed.core.common.error.CommonException
import io.lackstudio.omnifeed.core.network.error.RemoteException

@PublishedApi
internal val logger = Logger.withTag("RemoteHandler")

suspend inline fun <T> toResult(
    name: String = "UnknownResult",
    call: suspend () -> T
): Result<T> {
    logger.d { "toResult $name" }
    return try {
        Result.success(call())
    } catch (e: ResponseException) { // Handle response errors
        logger.e(e) { "ResponseException in toResult" }
        val errorBody = e.response.bodyAsText()
        val status = e.response.status
        val exception = when(e) {
            is ClientRequestException -> { // Handle 4xx errors
                when (status) {
                    HttpStatusCode.BadRequest ->
                        RemoteException.Api.BadRequest(cause = e, errorBody = errorBody)
                    HttpStatusCode.Unauthorized ->
                        RemoteException.Api.Unauthorized(cause = e, errorBody = errorBody,)
                    HttpStatusCode.Forbidden ->
                        RemoteException.Api.Forbidden(cause = e, errorBody = errorBody)
                    HttpStatusCode.NotFound ->
                        RemoteException.Api.NotFound(cause = e, errorBody = errorBody)
                    HttpStatusCode.TooManyRequests ->
                        RemoteException.Api.TooManyRequests(cause = e, errorBody = errorBody)
                    else ->
                        RemoteException.Api.UnexpectedStatus(
                            code = status.value,
                            cause = e,
                            errorBody = errorBody
                        )
                }
            }
            is ServerResponseException ->
                RemoteException.Api.Server(code = status.value, cause = e, errorBody = errorBody)
            else ->
                RemoteException.Api.UnexpectedStatus(
                    code =status.value,
                    cause = e,
                    errorBody = errorBody
                )
        }
        Result.failure(exception)
    } catch (e: Exception) { // Catch all other unexpected errors
        logger.e(e) { "Exception in toResult" }
        val exception = when (e) {
            is RemoteException -> e
            is CommonException -> e
            is HttpRequestTimeoutException,
            is ConnectTimeoutException,
            is SocketTimeoutException -> RemoteException.Network.Timeout(cause = e)
            is JsonConvertException,
            is SerializationException -> CommonException.Parsing.SerializationFailed(cause = e,)
            is IOException -> RemoteException.Network.Unknown(cause = e)
            else -> RemoteException.RemoteUnknown(cause = e)
        }
        Result.failure(exception)
    }
}
