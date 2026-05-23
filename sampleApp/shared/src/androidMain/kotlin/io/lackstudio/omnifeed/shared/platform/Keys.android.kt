package io.lackstudio.omnifeed.shared.platform

import io.lackstudio.omnifeed.shared.config.BuildKonfig

actual fun getUnsplashAccessKey() = BuildKonfig.UNSPLASH_ACCESS_KEY
actual fun getUnsplashSecretKey() = BuildKonfig.UNSPLASH_SECRET_KEY
