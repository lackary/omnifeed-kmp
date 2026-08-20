package io.lackstudio.omnifeed.auth.data.remote.source

import io.lackstudio.omnifeed.auth.data.error.AuthApiException
import io.lackstudio.omnifeed.core.network.error.RemoteException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class AuthRemoteHandlerTest {

    @Test
    fun handleAuthApi_withSuccess_returnsValue() = runTest {
        val expected = "Success"
        val result = handleAuthApi {
            expected
        }
        assertEquals(expected, result)
    }

    @Test
    fun handleAuthApi_withGenericException_wrapsAndThrows() = runTest {
        val expectedException = RuntimeException("General error")
        val actual = assertFailsWith<RemoteException.RemoteUnknown> {
            handleAuthApi {
                throw expectedException
            }
        }
        assertSame(expectedException, actual.cause)
    }

    @Test
    fun handleAuthApi_withRemoteApiError_andValidJson_throwsAuthApiException() = runTest {
        val errorJson = """
            {
                "error": {
                    "code": 401,
                    "message": "Invalid token"
                }
            }
        """.trimIndent()
        
        val apiException = RemoteException.Api.Unauthorized(
            errorBody = errorJson
        )

        val actual = assertFailsWith<AuthApiException> {
            handleAuthApi {
                // In a real scenario, toResult would catch a ResponseException and throw this Api exception.
                // Since handleAuthApi calls toResult, and we want to test handleAuthApi's catch block,
                // we need to make handleAuthApi's call to toResult return a Result.failure or have toResult throw.
                // However, handleAuthApi implementation calls getOrElse on Result.
                // Wait, handleAuthApi implementation is:
                // val result = toResult(name = name, call = call)
                // return result.getOrElse { exception -> ... throw exception }
                
                // So if we make call() throw apiException, toResult(call) will return Result.failure(apiException).
                throw apiException
            }
        }

        assertEquals("Invalid token", actual.structuredMessage)
        assertEquals(apiException, actual.originalApiException)
        assertEquals(401, actual.apiError?.error?.code)
    }

    @Test
    fun handleAuthApi_withRemoteApiError_andInvalidJson_throwsOriginalException() = runTest {
        val invalidJson = "Not a JSON"
        val apiException = RemoteException.Api.BadRequest(
            errorBody = invalidJson
        )

        val actual = assertFailsWith<RemoteException.Api.BadRequest> {
            handleAuthApi {
                throw apiException
            }
        }
        assertSame(apiException, actual)
    }

    @Test
    fun handleAuthApi_withRemoteApiError_andEmptyBody_throwsOriginalException() = runTest {
        val apiException = RemoteException.Api.Forbidden(
            errorBody = ""
        )

        val actual = assertFailsWith<RemoteException.Api.Forbidden> {
            handleAuthApi {
                throw apiException
            }
        }
        assertSame(apiException, actual)
    }

    @Test
    fun handleAuthApi_withNonApiRemoteException_throwsOriginal() = runTest {
        val networkException = RemoteException.Network.Timeout("Timeout")

        val actual = assertFailsWith<RemoteException.Network.Timeout> {
            handleAuthApi {
                throw networkException
            }
        }
        assertSame(networkException, actual)
    }
}
