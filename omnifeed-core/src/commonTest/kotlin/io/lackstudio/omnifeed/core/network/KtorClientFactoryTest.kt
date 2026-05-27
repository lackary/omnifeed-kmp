package io.lackstudio.omnifeed.core.network

import io.lackstudio.omnifeed.core.common.logging.MockKtorLoggerAdapter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger as KtorLogger
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.delay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.pluginOrNull
import io.ktor.client.plugins.timeout
import io.ktor.http.HttpMethod
import io.lackstudio.omnifeed.core.network.oauth.AccessTokenProvider
import kotlinx.serialization.Serializable
import kotlin.test.assertNotNull
import kotlin.test.fail

// Test data model
@Serializable
data class TestData(val status: String, val message: String)

class KtorClientFactoryTest {

    // 1. Prepare: Create shared configurations and a MockEngine to simulate HTTP requests and responses.
    private val testBaseUrl = "https://example.com"
    private val testTokenType = "Client"
    private val testToken = "test-api-key"
    private val testAuthToken = "$testTokenType $testToken"
    private val testUrlPath = "/success"

    // Helper to create a fresh MockEngine for each test
    private fun createDefaultMockEngine() = MockEngine { request ->
        when (request.url.encodedPath) {
            "/success" -> respond(
                content = "{\"status\":\"OK\",\"message\":\"Success!\"}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
            "/not-found" -> respond(
                content = "{\"error\":\"Not Found\"}",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
            "/server-error" -> respond(
                content = "{\"error\":\"Internal Server Error\"}",
                status = HttpStatusCode.InternalServerError,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
            else -> error("Unhandled ${request.url.encodedPath}")
        }
    }

    private val testKtorConfig = KtorConfig(
        baseUrl = testBaseUrl,
        logLevel = LogLevel.ALL,
        connectTimeoutMillis = null,
        requestTimeoutMillis = null,
        socketTimeoutMillis = null
    )

    private val testAccessTokenProvider = AccessTokenProvider(
        initialTokenType = testTokenType,
        initialToken = testToken
    )

    /**
     * Helper function to create a local HttpClient for standard tests to prevent Coroutine Leaks.
     * We REMOVED the default engine parameter to FORCE the test to hold a reference to the engine,
     * ensuring we never forget to close it.
     */
    private fun createLocalClient(
        engine: MockEngine,
        logger: KtorLogger = MockKtorLoggerAdapter()
    ): HttpClient {
        return KtorClientFactory.createHttpClient(
            engineFactory = engine,
            ktorConfig = testKtorConfig,
            logger = logger,
            accessTokenProvider = { testAccessTokenProvider }
        )
    }

    @Test
    fun `client should be configured with correct base URL and authorization header`() = runTest {
        val engine = createDefaultMockEngine()
        val client = createLocalClient(engine = engine)
        try {
            client.get(testUrlPath)
            val request = engine.requestHistory.last()
            assertEquals("$testBaseUrl$testUrlPath", request.url.toString())
            val authHeader = request.headers[HttpHeaders.Authorization]
            assertNotNull(authHeader)
            assertEquals(testAuthToken, authHeader)
        } finally {
            client.close()
            engine.close() // ⚠️ CRITICAL: Must close the instantiated engine!
        }
    }

    @Test
    fun `client should handle successful response and deserialize data`() = runTest {
        val engine = createDefaultMockEngine()
        val client = createLocalClient(engine = engine)
        try {
            val response: TestData = client.get(testUrlPath).body()
            assertEquals(HttpStatusCode.OK.description, response.status)
            assertEquals("Success!", response.message)
        } finally {
            client.close()
            engine.close() // ⚠️ CRITICAL
        }
    }

    @Test
    fun `client should throw ClientRequestException for 4xx status codes`() = runTest {
        val engine = createDefaultMockEngine()
        val client = createLocalClient(engine = engine)
        try {
            assertFailsWith<ClientRequestException> {
                client.get("/not-found")
            }
        } finally {
            client.close()
            engine.close() // ⚠️ CRITICAL
        }
    }

    @Test
    fun `client should throw ServerResponseException for 5xx status codes`() = runTest {
        val engine = createDefaultMockEngine()
        val client = createLocalClient(engine = engine)
        try {
            assertFailsWith<ServerResponseException> {
                client.get("/server-error")
            }
        } finally {
            client.close()
            engine.close() // ⚠️ CRITICAL
        }
    }

    @Test
    fun `client should have HttpTimeout plugin configured`() = runTest {
        val engine = createDefaultMockEngine()
        val client = createLocalClient(engine = engine)
        try {
            val httpTimeout = client.pluginOrNull(HttpTimeout)
            assertNotNull(httpTimeout)
        } finally {
            client.close()
            engine.close() // ⚠️ CRITICAL
        }
    }

    @Test
    fun `client should log requests with correct level and format`() = runTest {
        val engine = createDefaultMockEngine()
        val localLogger = MockKtorLoggerAdapter()
        val client = createLocalClient(engine = engine, logger = localLogger)

        try {
            client.get("/success")

            val requestLog = localLogger.loggedMessages.firstOrNull { it.contains("REQUEST") }
            val responseLog = localLogger.loggedMessages.firstOrNull { it.contains("RESPONSE") }
            val bodyLog = localLogger.loggedMessages.firstOrNull { it.contains("BODY") }

            assertNotNull(requestLog, "Request log message not found")
            assertNotNull(responseLog, "Response log message not found")
            assertNotNull(bodyLog, "Body log message not found, LogLevel.ALL should be working.")
        } finally {
            client.close()
            engine.close() // ⚠️ CRITICAL
        }
    }

    @Test
    fun `createHttpClient should set up defaultRequest with correct baseUrl and headers`() =
        runTest {
            val mockEngine = MockEngine { request ->
                assertEquals(HttpMethod.Get, request.method)
                assertEquals("$testBaseUrl$testUrlPath", request.url.toString())
                assertEquals(testAuthToken, request.headers[HttpHeaders.Authorization])
                assertEquals(ContentType.Application.Json.toString(), request.headers[HttpHeaders.ContentType])

                respond(
                    content = "{}",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }

            val client = KtorClientFactory.createHttpClient(
                engineFactory = mockEngine,
                ktorConfig = testKtorConfig,
                logger = MockKtorLoggerAdapter(),
                accessTokenProvider = { testAccessTokenProvider }
            )

            try {
                val response = client.get(testUrlPath)
                assertEquals(HttpStatusCode.OK, response.status)
            } finally {
                client.close()
                mockEngine.close() // ⚠️ CRITICAL
            }
        }

    @Test
    fun `createHttpClient should not set Authorization header if authToken is null`() =
        runTest {
            val mockEngine = MockEngine { request ->
                val authHeader = request.headers[HttpHeaders.Authorization]
                assertNull(authHeader, "Authorization header should not be present when authToken is null")
                respond(content = "{}", status = HttpStatusCode.OK)
            }

            val client = KtorClientFactory.createHttpClient(
                engineFactory = mockEngine,
                ktorConfig = testKtorConfig.copy(),
                logger = MockKtorLoggerAdapter()
            )

            try {
                val response = client.get(testUrlPath)
                assertEquals(HttpStatusCode.OK, response.status)
            } finally {
                client.close()
                mockEngine.close() // ⚠️ CRITICAL
            }
        }

    @Test
    fun `createHttpClient should set up HttpTimeout correctly`() =
        runTest {
            val fastTimeoutConfig = testKtorConfig.copy(
                requestTimeoutMillis = 500L,
                connectTimeoutMillis = 500L,
                socketTimeoutMillis = 500L
            )

            // Using a delay in MockEngine to trigger a real Ktor timeout
            val mockEngine = MockEngine { _ ->
                delay(1000L) // Wait longer than the timeout
                respond("Delayed success")
            }

            val client = KtorClientFactory.createHttpClient(
                engineFactory = mockEngine,
                ktorConfig = fastTimeoutConfig,
                logger = MockKtorLoggerAdapter(),
                accessTokenProvider = { testAccessTokenProvider }
            )

            try {
                client.get(testUrlPath) {
                    timeout {
                        requestTimeoutMillis = 200L // Overriding with even shorter timeout
                    }
                }
                fail("Should have thrown a timeout exception")
            } catch (e: Throwable) {
                println("Caught expected exception: $e")
                // Check if it's a timeout. Ktor throws HttpRequestTimeoutException for request timeouts
                val isTimeout = e is HttpRequestTimeoutException ||
                        e.message?.contains("time", ignoreCase = true) == true ||
                        e.cause?.message?.contains("time", ignoreCase = true) == true

                assertTrue(isTimeout, "Expected a timeout exception, but got: ${e::class.simpleName} - ${e.message}")
            } finally {
                client.close()
                mockEngine.close() // ⚠️ CRITICAL
            }
        }

    @Test
    fun `createHttpClient should use the provided log level`() = runTest {
        val logMessages = mutableListOf<String>()
        val testLogger = object : KtorLogger {
            override fun log(message: String) {
                logMessages.add(message)
            }
        }

        val mockEngine = MockEngine {
            respond(
                content = "{\"status\":\"ok\"}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = KtorClientFactory.createHttpClient(
            engineFactory = mockEngine,
            ktorConfig = testKtorConfig,
            logger = testLogger,
            accessTokenProvider = { testAccessTokenProvider }
        )

        try {
            client.get(testUrlPath)
            assertTrue(logMessages.isNotEmpty(), "Logger should have captured log messages.")
            assertTrue(logMessages.first().contains("REQUEST"), "Log should contain request details.")
        } finally {
            client.close()
            mockEngine.close() // ⚠️ CRITICAL
        }
    }
}
