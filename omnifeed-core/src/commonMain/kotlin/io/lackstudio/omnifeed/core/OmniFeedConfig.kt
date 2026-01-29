package io.lackstudio.omnifeed.core

import co.touchlab.kermit.Logger

/**
 * 1. Defines the Unsplash-specific configuration data structure.
 * Although these are Unsplash settings, they are defined in Core for unified access.
 */
data class UnsplashConfig(
    val token: String,
    val tokenType: String = "Client-ID"
)

/**
 * 2. Main configuration entry point for the SDK.
 */
data class OmniFeedConfig(
    // General settings
    val appLogger: Logger? = null,

    // Module settings (required to be provided by the App)
    val unsplash: UnsplashConfig
)
