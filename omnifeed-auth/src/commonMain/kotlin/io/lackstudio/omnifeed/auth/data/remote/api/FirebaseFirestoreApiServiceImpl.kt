package io.lackstudio.omnifeed.auth.data.remote.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.COLLECTION_USERS
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.DATABASE_DEFAULT
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.PATH_DATABASES
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.PATH_DOCUMENTS
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.PATH_PROJECTS
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.VERSION_V1
import kotlinx.serialization.json.*

class FirebaseFirestoreApiServiceImpl(
    private val httpClient: HttpClient,
) : FirebaseFirestoreApiService {

    override suspend fun saveFirestoreProfile(
        projectId: String,
        uid: String,
        idToken: String,
        fields: Map<String, Any?>,
        fieldPaths: List<String>?
    ) {
        val requestBody = buildJsonObject {
            putJsonObject("fields") {
                fields.forEach { (key, value) ->
                    put(key, toFirestoreValue(value))
                }
            }
        }

        val path = "/$VERSION_V1/$PATH_PROJECTS/$projectId/$PATH_DATABASES/$DATABASE_DEFAULT/$PATH_DOCUMENTS/$COLLECTION_USERS/$uid"

        val response = httpClient.patch(path) {
            header("Authorization", "Bearer $idToken")
            val paths = fieldPaths ?: fields.keys.toList()
            paths.forEach { p ->
                parameter("updateMask.fieldPaths", p)
            }
            setBody(requestBody)
        }
    }

    override suspend fun getFirestoreProfile(
        projectId: String,
        uid: String,
        idToken: String
    ): Map<String, Any?>? {
        val path = "/$VERSION_V1/$PATH_PROJECTS/$projectId/$PATH_DATABASES/$DATABASE_DEFAULT/$PATH_DOCUMENTS/$COLLECTION_USERS/$uid"
        
        try {
            val response = httpClient.get(path) {
                header("Authorization", "Bearer $idToken")
            }
            val body = response.body<JsonObject>()
            val fields = body["fields"]?.jsonObject ?: return emptyMap()
            return fromFirestoreFields(fields)
        } catch (e: Exception) {
            // Handle 404 explicitly if it's a ClientRequestException (handled by expectSuccess=true)
            // But wait, toResult in handleAuthApi might catch this.
            // For now, I'll keep the 404 check if I can access the status.
            throw e
        }
    }

    override suspend fun deleteFirestoreProfile(projectId: String, uid: String, idToken: String) {
        val path = "/$VERSION_V1/$PATH_PROJECTS/$projectId/$PATH_DATABASES/$DATABASE_DEFAULT/$PATH_DOCUMENTS/$COLLECTION_USERS/$uid"
        httpClient.delete(path) {
            header("Authorization", "Bearer $idToken")
        }
    }

    private fun toFirestoreValue(value: Any?): JsonObject {
        return buildJsonObject {
            when (value) {
                is String -> put("stringValue", value)
                is Boolean -> put("booleanValue", value)
                is Map<*, *> -> {
                    putJsonObject("mapValue") {
                        putJsonObject("fields") {
                            value.forEach { (k, v) ->
                                put(k.toString(), toFirestoreValue(v))
                            }
                        }
                    }
                }
                else -> put("stringValue", value?.toString() ?: "")
            }
        }
    }

    private fun fromFirestoreFields(fields: JsonObject): Map<String, Any?> {
        return fields.mapValues { (_, value) ->
            val obj = value.jsonObject
            when {
                "stringValue" in obj -> obj["stringValue"]?.jsonPrimitive?.content
                "booleanValue" in obj -> obj["booleanValue"]?.jsonPrimitive?.booleanOrNull
                "mapValue" in obj -> {
                    val innerFields = obj["mapValue"]?.jsonObject?.get("fields")?.jsonObject
                    if (innerFields != null) fromFirestoreFields(innerFields) else emptyMap<String, Any?>()
                }
                else -> null
            }
        }
    }
}
