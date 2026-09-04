package io.lackstudio.omnifeed.auth.domain.model

/**
 * Represents the identity providers supported by the system.
 * This is a domain-level enum that decouples business logic from technical implementation details.
 */
enum class AuthProvider(
    val id: String,         // Internal domain key (e.g., "google")
    val firebaseId: String  // Technical provider ID for Firebase (e.g., "google.com")
) {
    GOOGLE("google", "google.com"),
    APPLE("apple", "apple.com"),
    FACEBOOK("facebook", "facebook.com"),
    GITHUB("github", "github.com"),
    PASSWORD("password", "password");

    companion object {
        /**
         * Safely finds an [AuthProvider] by its Firebase provider ID.
         */
        fun fromFirebaseId(firebaseId: String?): AuthProvider? =
            entries.find { it.firebaseId == firebaseId }

        /**
         * Safely finds an [AuthProvider] by its internal domain ID.
         */
        fun fromId(id: String?): AuthProvider? =
            entries.find { it.id == id }
    }
}
