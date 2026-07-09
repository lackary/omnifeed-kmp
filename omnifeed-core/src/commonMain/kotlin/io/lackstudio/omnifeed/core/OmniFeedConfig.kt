package io.lackstudio.omnifeed.core

import co.touchlab.kermit.Logger

/**
 * 1. Defines the Unsplash-specific configuration data structure.
 */
data class UnsplashConfig(
    val token: String,
    val tokenType: String = "Client-ID"
)

/**
 * 2. Configuration for a custom authentication service.
 */
data class CustomServiceConfig(
    val authEndpoint: String
)

/**
 * 3. Main configuration entry point for the SDK.
 */
data class OmniFeedConfig(
    // General settings
    val appLogger: Logger? = null,

    // Module settings
    val unsplash: UnsplashConfig,

    // Auth settings for multiple custom services (e.g., "unsplash", "github")
    val customServices: Map<String, CustomServiceConfig> = emptyMap()
)
