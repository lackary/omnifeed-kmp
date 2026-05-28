package io.lackstudio.omnifeed.auth.domain.model

data class User(
    val id: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)
