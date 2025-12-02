package io.lackstudio.omnifeed.app.platform

import io.lackstudio.omnifeed.app.config.BuildKonfig

actual fun getUnsplashAccessKey() = BuildKonfig.UNSPLASH_ACCESS_KEY
actual fun getUnsplashSecretKey() = BuildKonfig.UNSPLASH_SECRET_KEY
