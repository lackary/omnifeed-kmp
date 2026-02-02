package io.lackstudio.omnifeed.core.common.util

import co.touchlab.kermit.Logger
import java.lang.management.ManagementFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val logger = Logger.withTag("PlatformProvider")

actual val isDebuggable: Boolean
    get() = true

actual fun getCurrentTimestamp(format: String): String {
    try {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(Date())
    } catch (e: IllegalArgumentException) {
        logger.e(e) { "Invalid date format pattern: $format. Using default format 'yyyy-MM-dd HH:mm:ss'." }
        val defaultFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return defaultFormat.format(Date())
    }
}

actual fun getCurrentProcessId(): String {
    val jvmName = ManagementFactory.getRuntimeMXBean().name
    return jvmName.split("@").firstOrNull() ?: ""
}

actual fun getCurrentThreadId(): String {
    return Thread.currentThread().threadId().toString()
}
