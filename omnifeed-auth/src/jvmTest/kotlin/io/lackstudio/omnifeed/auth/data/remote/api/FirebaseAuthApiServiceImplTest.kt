package io.lackstudio.omnifeed.auth.data.remote.api

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.lackstudio.omnifeed.auth.data.remote.model.request.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class FirebaseAuthApiServiceImplTest {

    private lateinit var mockEngine: MockEngine
    private lateinit var service: FirebaseAuthApiServiceImpl

    @BeforeTest
    fun setup() {
        mockEngine = MockEngine { _ ->
            respond(
                content = "{}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }

        service = FirebaseAuthApiServiceImpl(httpClient)
    }

    @Test
    fun `fetchFirebaseCustomToken should return token from map`() = runTest {
        // Arrange
        val expectedToken = "custom-token-123"
        mockEngine = MockEngine { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("https://api.test.com/auth", request.url.toString())
            
            respond(
                content = """{"custom_token": "$expectedToken"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        service = FirebaseAuthApiServiceImpl(HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            defaultRequest { contentType(ContentType.Application.Json) }
        })

        // Act
        val result = service.fetchFirebaseCustomToken("https://api.test.com/auth", "access-token", "google")

        // Assert
        assertEquals(expectedToken, result)
    }

    @Test
    fun `signInWithIdp should send correct request`() = runTest {
        // Arrange
        val idToken = "firebase-id-token"
        mockEngine = MockEngine { request ->
            assertTrue(request.url.encodedPath.contains("signInWithIdp"))
            respond(
                content = """{"idToken": "$idToken", "localId": "uid123"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        service = FirebaseAuthApiServiceImpl(HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            defaultRequest { contentType(ContentType.Application.Json) }
        })

        // Act
        val response = service.signInWithIdp(SignInWithIdpRequest(postBody = "id_token=abc&providerId=google.com"))

        // Assert
        assertEquals(idToken, response.idToken)
        assertEquals("uid123", response.localId)
    }

    @Test
    fun `lookup should send correct request`() = runTest {
        // Arrange
        mockEngine = MockEngine { request ->
            assertTrue(request.url.encodedPath.contains("lookup"))
            respond(
                content = """{"users": [{"localId": "uid123", "email": "test@test.com"}]}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        service = FirebaseAuthApiServiceImpl(HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            defaultRequest { contentType(ContentType.Application.Json) }
        })

        // Act
        val response = service.lookup(LookupRequest(idToken = "token"))

        // Assert
        assertEquals(1, response.users.size)
        assertEquals("uid123", response.users[0].localId)
    }

    @Test
    fun `signInWithCustomToken should send correct request`() = runTest {
        // Arrange
        val idToken = "firebase-id-token"
        mockEngine = MockEngine { request ->
            assertTrue(request.url.encodedPath.contains("signInWithCustomToken"))
            respond(
                content = """{"idToken": "$idToken"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        service = FirebaseAuthApiServiceImpl(HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            defaultRequest { contentType(ContentType.Application.Json) }
        })

        // Act
        val response = service.signInWithCustomToken(SignInWithCustomTokenRequest(token = "custom-token"))

        // Assert
        assertEquals(idToken, response.idToken)
    }

    @Test
    fun `updateAccount should send correct request`() = runTest {
        // Arrange
        val idToken = "updated-id-token"
        mockEngine = MockEngine { request ->
            assertTrue(request.url.encodedPath.contains("update"))
            respond(
                content = """{"idToken": "$idToken"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        service = FirebaseAuthApiServiceImpl(HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            defaultRequest { contentType(ContentType.Application.Json) }
        })

        // Act
        val response = service.updateAccount(UpdateAccountRequest(idToken = "old-token", displayName = "New Name"))

        // Assert
        assertEquals(idToken, response.idToken)
    }

    @Test
    fun `deleteAccount should send correct request`() = runTest {
        // Arrange
        var requestPath = ""
        mockEngine = MockEngine { request ->
            requestPath = request.url.encodedPath
            respond(
                content = "{}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        service = FirebaseAuthApiServiceImpl(HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            defaultRequest { contentType(ContentType.Application.Json) }
        })

        // Act
        service.deleteAccount(DeleteAccountRequest(idToken = "token-to-delete"))

        // Assert
        assertTrue(requestPath.contains("delete"))
    }
}
