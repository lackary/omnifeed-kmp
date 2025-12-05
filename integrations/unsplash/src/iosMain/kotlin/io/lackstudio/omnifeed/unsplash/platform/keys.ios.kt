package io.lackstudio.omnifeed.unsplash.platform

import io.lackstudio.omnifeed.unsplash.config.BuildKonfig

internal actual fun getUnsplashAccessKey() = BuildKonfig.UNSPLASH_ACCESS_KEY
internal actual fun getUnsplashSecretKey() = BuildKonfig.UNSPLASH_SECRET_KEY
