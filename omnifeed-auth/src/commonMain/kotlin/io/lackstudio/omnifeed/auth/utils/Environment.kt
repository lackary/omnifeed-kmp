package io.lackstudio.omnifeed.auth.utils

object Environment {
    const val GOOGLE_CLOUD_API_URL = "https://identitytoolkit.googleapis.com"
    const val GOOGLE_CLOUD_HTTP_HEADER_X_GOOGLE_API_KEY = "X-goog-api-key"
    const val GOOGLE_CLOUD_API_V1 = "v1"
    const val GOOGLE_CLOUD_API_V2 = "v2"

    private const val RESOURCE_ACCOUNTS = "accounts"

    const val ENDPOINT_ACCOUNTS_SIGN_IN_WITH_IDP = "$RESOURCE_ACCOUNTS:signInWithIdp"
    const val ENDPOINT_ACCOUNTS_SIGN_IN_WITH_CUSTOM_TOKEN = "$RESOURCE_ACCOUNTS:signInWithCustomToken"
    const val ENDPOINT_ACCOUNTS_LOOKUP = "$RESOURCE_ACCOUNTS:lookup"
    const val ENDPOINT_ACCOUNTS_DELETE = "$RESOURCE_ACCOUNTS:delete"
}
