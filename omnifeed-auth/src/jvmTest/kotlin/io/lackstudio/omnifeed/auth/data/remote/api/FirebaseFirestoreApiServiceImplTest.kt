package io.lackstudio.omnifeed.auth.data.remote.api

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class FirebaseFirestoreApiServiceImplTest {

    private lateinit var mockEngine: MockEngine
    private lateinit var httpClient: HttpClient
    private lateinit var service: FirebaseFirestoreApiServiceImpl

    private val projectId = "test-project"
    private val uid = "test-uid"
    private val idToken = "test-id-token"

    @BeforeTest
    fun setup() {
        mockEngine = MockEngine { request ->
            respond(
                content = "{}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }

        service = FirebaseFirestoreApiServiceImpl(httpClient)
    }

    @Test
    fun `saveFirestoreProfile should send correct patch request`() = runTest {
        // Arrange
        val fields = mapOf(
            "name" to "OmniFeed",
            "active" to true,
            "count" to null,
            "metadata" to mapOf("version" to "1.0")
        )
        
        mockEngine = MockEngine { request ->
            // Verify URL and Method
            assertEquals(HttpMethod.Patch, request.method)
            assertTrue(request.url.encodedPath.contains("/projects/$projectId/databases/(default)/documents/users/$uid"))
            
            // Verify Headers
            assertEquals("Bearer $idToken", request.headers[HttpHeaders.Authorization])
            
            // Verify Query Parameters (updateMask.fieldPaths)
            val params = request.url.parameters.getAll("updateMask.fieldPaths")
            assertNotNull(params)
            assertTrue(params.containsAll(listOf("name", "active", "count", "metadata")))

            // Verify Body (Firestore format)
            val body = request.body
            val bodyString = if (body is TextContent) body.text else ""
            
            if (bodyString.isNotEmpty()) {
                val bodyJson = Json.parseToJsonElement(bodyString).jsonObject
                val fieldsJson = bodyJson["fields"]?.jsonObject
                
                assertNotNull(fieldsJson)
                assertEquals("OmniFeed", fieldsJson["name"]?.jsonObject?.get("stringValue")?.jsonPrimitive?.content)
                assertEquals(true, fieldsJson["active"]?.jsonObject?.get("booleanValue")?.jsonPrimitive?.boolean)
                assertEquals(true, fieldsJson["count"]?.jsonObject?.containsKey("nullValue"))
                
                val metadataFields = fieldsJson["metadata"]?.jsonObject?.get("mapValue")?.jsonObject?.get("fields")?.jsonObject
                assertEquals("1.0", metadataFields?.get("version")?.jsonObject?.get("stringValue")?.jsonPrimitive?.content)
            } else {
                fail("Request body is empty or not TextContent")
            }

            respond(
                content = "{}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        
        service = FirebaseFirestoreApiServiceImpl(HttpClient(mockEngine) {
            install(ContentNegotiation) { json() }
            defaultRequest { contentType(ContentType.Application.Json) }
        })

        // Act
        service.saveFirestoreProfile(projectId, uid, idToken, fields)
    }

    @Test
    fun `getFirestoreProfile should parse firestore format correctly`() = runTest {
        // Arrange
        val firestoreResponse = """
            {
                "name": "projects/$projectId/databases/(default)/documents/users/$uid",
                "fields": {
                    "displayName": { "stringValue": "John Doe" },
                    "isPremium": { "booleanValue": true },
                    "lastLogin": { "nullValue": null },
                    "preferences": {
                        "mapValue": {
                            "fields": {
                                "theme": { "stringValue": "dark" }
                            }
                        }
                    }
                }
            }
        """.trimIndent()

        mockEngine = MockEngine { request ->
            assertEquals(HttpMethod.Get, request.method)
            respond(
                content = firestoreResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        service = FirebaseFirestoreApiServiceImpl(HttpClient(mockEngine) {
            install(ContentNegotiation) { json() }
            defaultRequest { contentType(ContentType.Application.Json) }
        })

        // Act
        val result = service.getFirestoreProfile(projectId, uid, idToken)

        // Assert
        assertNotNull(result)
        assertEquals("John Doe", result["displayName"])
        assertEquals(true, result["isPremium"])
        assertNull(result["lastLogin"])
        
        val preferences = result["preferences"] as? Map<*, *>
        assertNotNull(preferences)
        assertEquals("dark", preferences["theme"])
    }

    @Test
    fun `deleteFirestoreProfile should send delete request`() = runTest {
        // Arrange
        var deleteCalled = false
        mockEngine = MockEngine { request ->
            if (request.method == HttpMethod.Delete) {
                deleteCalled = true
                assertTrue(request.url.encodedPath.contains(uid))
            }
            respond("", HttpStatusCode.NoContent)
        }

        service = FirebaseFirestoreApiServiceImpl(HttpClient(mockEngine))

        // Act
        service.deleteFirestoreProfile(projectId, uid, idToken)

        // Assert
        assertTrue(deleteCalled)
    }
}
