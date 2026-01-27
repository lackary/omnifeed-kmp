package io.lackstudio.omnifeed.core.common.util

expect val isDebuggable: Boolean

expect fun getCurrentTimestamp(format: String): String

expect fun getCurrentProcessId(): String

expect fun getCurrentThreadId(): String
