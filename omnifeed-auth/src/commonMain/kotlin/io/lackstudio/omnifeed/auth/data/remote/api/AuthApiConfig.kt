package io.lackstudio.omnifeed.auth.data.remote.api

object AuthApiConfig {
    // --- Base URLs ---
    const val IDENTITY_BASE_URL = "https://identitytoolkit.googleapis.com"
    const val FIRESTORE_BASE_URL = "https://firestore.googleapis.com"

    // --- Headers ---
    const val HEADER_X_GOOGLE_API_KEY = "X-goog-api-key"

    // --- Versions ---
    const val VERSION_V1 = "v1"
    const val VERSION_V2 = "v2"

    // --- Endpoints ---
    private const val RESOURCE_ACCOUNTS = "accounts"
    const val ENDPOINT_SIGN_IN_WITH_IDP = "$RESOURCE_ACCOUNTS:signInWithIdp"
    const val ENDPOINT_SIGN_IN_WITH_CUSTOM_TOKEN = "$RESOURCE_ACCOUNTS:signInWithCustomToken"
    const val ENDPOINT_LOOKUP = "$RESOURCE_ACCOUNTS:lookup"
    const val ENDPOINT_UPDATE = "$RESOURCE_ACCOUNTS:update"
    const val ENDPOINT_DELETE = "$RESOURCE_ACCOUNTS:delete"

    // --- Firestore Paths ---
    const val PATH_PROJECTS = "projects"
    const val PATH_DATABASES = "databases"
    const val DATABASE_DEFAULT = "(default)"
    const val PATH_DOCUMENTS = "documents"
    const val COLLECTION_USERS = "users"
}
