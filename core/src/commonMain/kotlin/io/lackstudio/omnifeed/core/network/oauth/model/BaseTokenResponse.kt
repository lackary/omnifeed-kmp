package io.lackstudio.omnifeed.core.network.oauth.model

interface BaseTokenResponse {
    val accessToken: String
    val tokenType: String
    val refreshToken: String?
    val scope: String?
}
