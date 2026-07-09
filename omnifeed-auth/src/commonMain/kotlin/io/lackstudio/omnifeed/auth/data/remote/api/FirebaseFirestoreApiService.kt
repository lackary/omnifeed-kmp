package io.lackstudio.omnifeed.auth.data.remote.api

interface FirebaseFirestoreApiService {
    suspend fun saveFirestoreProfile(
        projectId: String,
        uid: String,
        idToken: String,
        fields: Map<String, Any?>,
        fieldPaths: List<String>? = null
    )

    suspend fun getFirestoreProfile(
        projectId: String,
        uid: String,
        idToken: String
    ): Map<String, Any?>?

    suspend fun deleteFirestoreProfile(
        projectId: String,
        uid: String,
        idToken: String
    )
}
