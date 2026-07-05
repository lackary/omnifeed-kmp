package io.lackstudio.omnifeed.auth.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.lackstudio.omnifeed.auth.data.remote.model.request.*
import io.lackstudio.omnifeed.auth.data.remote.model.response.*
import io.lackstudio.omnifeed.auth.utils.Environment.ENDPOINT_ACCOUNTS_DELETE
import io.lackstudio.omnifeed.auth.utils.Environment.ENDPOINT_ACCOUNTS_LOOKUP
import io.lackstudio.omnifeed.auth.utils.Environment.ENDPOINT_ACCOUNTS_SIGN_IN_WITH_CUSTOM_TOKEN
import io.lackstudio.omnifeed.auth.utils.Environment.ENDPOINT_ACCOUNTS_SIGN_IN_WITH_IDP
import io.lackstudio.omnifeed.auth.utils.Environment.GOOGLE_CLOUD_API_V1

class FirebaseApiServiceImpl(
    private val httpClient: HttpClient,
) : FirebaseApiService {

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

        if (response.status.value != 200) {
            throw Exception("Failed to fetch custom token: ${response.status}")
        }

        val body = response.body<Map<String, String>>()
        return body["custom_token"] ?: throw Exception("No custom token in response")
    }

    override suspend fun signInWithIdp(request: SignInWithIdpRequest): SignInWithIdpResponse {
        val response =
            httpClient.post("/$GOOGLE_CLOUD_API_V1/$ENDPOINT_ACCOUNTS_SIGN_IN_WITH_IDP") {
            setBody(request)
        }

        if (response.status.value != 200) {
            val errorBody = response.body<String>()
            throw Exception("Firebase REST signInWithIdp failed (${response.status}): $errorBody")
        }

        return response.body()
    }

    override suspend fun signInWithCustomToken(request: SignInWithCustomTokenRequest): SignInWithCustomTokenResponse {
        val response = httpClient.post("/$GOOGLE_CLOUD_API_V1/$ENDPOINT_ACCOUNTS_SIGN_IN_WITH_CUSTOM_TOKEN") {
            setBody(request)
        }

        if (response.status.value != 200) {
            val errorBody = response.body<String>()
            throw Exception("Firebase REST signInWithCustomToken failed (${response.status}): $errorBody")
        }

        return response.body()
    }

    override suspend fun lookup(request: LookupRequest): LookupResponse {
        val response = httpClient.post("/$GOOGLE_CLOUD_API_V1/$ENDPOINT_ACCOUNTS_LOOKUP") {
            setBody(request)
        }

        if (response.status.value != 200) {
            val errorBody = response.body<String>()
            throw Exception("Firebase REST lookup failed (${response.status}): $errorBody")
        }

        return response.body()
    }

    override suspend fun deleteAccount(request: DeleteAccountRequest) {
        val response = httpClient.post("/$GOOGLE_CLOUD_API_V1/$ENDPOINT_ACCOUNTS_DELETE") {
            setBody(request)
        }

        if (response.status.value != 200) {
            val errorBody = response.body<String>()
            throw Exception("Firebase REST delete failed (${response.status}): $errorBody")
        }
    }
}
