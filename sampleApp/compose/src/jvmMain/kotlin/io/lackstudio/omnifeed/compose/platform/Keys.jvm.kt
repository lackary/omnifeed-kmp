package io.lackstudio.omnifeed.compose.platform

import io.lackstudio.omnifeed.compose.config.BuildKonfig

actual fun getUnsplashAccessKey() = BuildKonfig.UNSPLASH_ACCESS_KEY
actual fun getUnsplashSecretKey() = BuildKonfig.UNSPLASH_SECRET_KEY
