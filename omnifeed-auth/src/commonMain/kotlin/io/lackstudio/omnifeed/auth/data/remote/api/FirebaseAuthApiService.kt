package io.lackstudio.omnifeed.auth.data.remote.api

import io.lackstudio.omnifeed.auth.data.remote.model.request.*
import io.lackstudio.omnifeed.auth.data.remote.model.response.*

interface FirebaseAuthApiService {
    suspend fun fetchFirebaseCustomToken(
        endpoint: String,
        customAccessToken: String,
        provider: String
    ): String

    suspend fun signInWithIdp(
        request: SignInWithIdpRequest
    ): SignInWithIdpResponse

    suspend fun signInWithCustomToken(
        request: SignInWithCustomTokenRequest
    ): SignInWithCustomTokenResponse

    suspend fun lookup(
        request: LookupRequest
    ): LookupResponse

    suspend fun deleteAccount(
        request: DeleteAccountRequest
    )
}
