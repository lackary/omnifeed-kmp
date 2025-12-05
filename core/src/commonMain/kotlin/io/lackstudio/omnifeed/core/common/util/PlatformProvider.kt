package io.lackstudio.omnifeed.core.common.util

import co.touchlab.kermit.LogWriter

expect val isDebuggable: Boolean

expect fun getCurrentTimestamp(format: String): String

expect fun getCurrentProcessId(): String

expect fun getCurrentThreadId(): String

expect fun appPlatformLogWriter(): LogWriter
