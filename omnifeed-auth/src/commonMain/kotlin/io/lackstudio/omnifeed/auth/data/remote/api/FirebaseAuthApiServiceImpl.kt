package io.lackstudio.omnifeed.auth.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.ENDPOINT_DELETE
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.ENDPOINT_LOOKUP
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.ENDPOINT_SIGN_IN_WITH_CUSTOM_TOKEN
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.ENDPOINT_SIGN_IN_WITH_IDP
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.ENDPOINT_UPDATE
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.VERSION_V1
import io.lackstudio.omnifeed.auth.data.remote.model.request.*
import io.lackstudio.omnifeed.auth.data.remote.model.response.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull

class FirebaseAuthApiServiceImpl(
    private val httpClient: HttpClient,
) : FirebaseAuthApiService {

    override suspend fun fetchFirebaseCustomToken(
        endpoint: String,
        customAccessToken: String,
        provider: String
    ): String {
        val response = httpClient.post(endpoint) {
            setBody(mapOf(
                "access_token" to customAccessToken,
                "provider" to provider
            ))
        }

        val body = response.body<Map<String, String>>()
        return body["custom_token"] ?: throw Exception("No custom token in response")
    }

    override suspend fun fetchCustomUserProfile(
        verifyUrl: String,
        accessToken: String
    ): CustomUserProfile {
        val response = httpClient.get(verifyUrl) {
            header("Authorization", "Bearer $accessToken")
        }
        val json = response.body<JsonObject>()
        
        val username = json["name"]?.jsonPrimitive?.contentOrNull
            ?: json["username"]?.jsonPrimitive?.contentOrNull
        val email = json["email"]?.jsonPrimitive?.contentOrNull
        
        val photoUrl = try {
            json["profile_image"]?.jsonObject?.get("large")?.jsonPrimitive?.contentOrNull
                ?: json["profile_image"]?.jsonObject?.get("medium")?.jsonPrimitive?.contentOrNull
                ?: json["photo_url"]?.jsonPrimitive?.contentOrNull
                ?: json["avatar_url"]?.jsonPrimitive?.contentOrNull
        } catch (_: Exception) { null }

        return CustomUserProfile(
            username = username,
            email = email,
            photoUrl = photoUrl
        )
    }

    override suspend fun signInWithIdp(request: SignInWithIdpRequest): SignInWithIdpResponse {
        return httpClient.post("/$VERSION_V1/$ENDPOINT_SIGN_IN_WITH_IDP") {
            setBody(request)
        }.body()
    }

    override suspend fun signInWithCustomToken(request: SignInWithCustomTokenRequest): SignInWithCustomTokenResponse {
        return httpClient.post("/$VERSION_V1/$ENDPOINT_SIGN_IN_WITH_CUSTOM_TOKEN") {
            setBody(request)
        }.body()
    }

    override suspend fun lookup(request: LookupRequest): LookupResponse {
        return httpClient.post("/$VERSION_V1/$ENDPOINT_LOOKUP") {
            setBody(request)
        }.body()
    }

    override suspend fun updateAccount(request: UpdateAccountRequest): SignInWithCustomTokenResponse {
        return httpClient.post("/$VERSION_V1/$ENDPOINT_UPDATE") {
            setBody(request)
        }.body()
    }

    override suspend fun deleteAccount(request: DeleteAccountRequest) {
        httpClient.post("/$VERSION_V1/$ENDPOINT_DELETE") {
            setBody(request)
        }
    }
}
