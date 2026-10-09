package io.lackstudio.omnifeed.auth.data.remote.api

import io.lackstudio.omnifeed.auth.data.remote.model.request.*
import io.lackstudio.omnifeed.auth.data.remote.model.response.*
import kotlinx.serialization.Serializable

@Serializable
data class CustomUserProfile(
    val username: String? = null,
    val email: String? = null,
    val photoUrl: String? = null
)

interface FirebaseAuthApiService {
    suspend fun fetchFirebaseCustomToken(
        endpoint: String,
        customAccessToken: String,
        provider: String
    ): String

    suspend fun fetchCustomUserProfile(
        verifyUrl: String,
        accessToken: String
    ): CustomUserProfile

    suspend fun signInWithIdp(
        request: SignInWithIdpRequest
    ): SignInWithIdpResponse

    suspend fun signInWithCustomToken(
        request: SignInWithCustomTokenRequest
    ): SignInWithCustomTokenResponse

    suspend fun lookup(
        request: LookupRequest
    ): LookupResponse

    suspend fun updateAccount(
        request: UpdateAccountRequest
    ): SignInWithCustomTokenResponse

    suspend fun deleteAccount(
        request: DeleteAccountRequest
    )
}
