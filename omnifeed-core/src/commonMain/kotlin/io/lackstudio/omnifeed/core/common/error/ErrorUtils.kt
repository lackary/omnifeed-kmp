package io.lackstudio.omnifeed.core.common.error

/**
 * Extension function to get a user-friendly error message from a Throwable.
 */
expect fun Throwable.getFriendlyMessage(): String
