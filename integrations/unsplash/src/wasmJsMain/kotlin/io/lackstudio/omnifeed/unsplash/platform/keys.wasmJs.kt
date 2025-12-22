package io.lackstudio.omnifeed.unsplash.platform

import io.lackstudio.omnifeed.unsplash.config.BuildKonfig

internal actual fun getUnsplashAccessKey(): String = BuildKonfig.UNSPLASH_ACCESS_KEY
internal actual fun getUnsplashSecretKey(): String = BuildKonfig.UNSPLASH_SECRET_KEY
