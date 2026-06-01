package io.lackstudio.omnifeed.core.common.error

actual fun Throwable.getFriendlyMessage(): String = message ?: "An unknown error occurred"
