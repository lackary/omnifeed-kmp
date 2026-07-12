package io.lackstudio.omnifeed.auth.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.ENDPOINT_DELETE
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.ENDPOINT_LOOKUP
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.ENDPOINT_SIGN_IN_WITH_CUSTOM_TOKEN
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.ENDPOINT_SIGN_IN_WITH_IDP
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.VERSION_V1
import io.lackstudio.omnifeed.auth.data.remote.model.request.*
import io.lackstudio.omnifeed.auth.data.remote.model.response.*

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

    override suspend fun deleteAccount(request: DeleteAccountRequest) {
        httpClient.post("/$VERSION_V1/$ENDPOINT_DELETE") {
            setBody(request)
        }
    }
}
