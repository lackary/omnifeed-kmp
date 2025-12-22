package io.lackstudio.omnifeed.core.common.util

import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.LogWriter

actual val isDebuggable: Boolean
    get() = true

actual fun getCurrentTimestamp(format: String): String {
    val date = Date() // This uses the external class defined below

    val year = date.getFullYear().toString()
    val month = (date.getMonth() + 1).toString().padStart(2, '0')
    val day = date.getDate().toString().padStart(2, '0')
    val hours = date.getHours().toString().padStart(2, '0')
    val minutes = date.getMinutes().toString().padStart(2, '0')
    val seconds = date.getSeconds().toString().padStart(2, '0')
    val milliseconds = date.getMilliseconds().toString().padStart(3, '0')

    return format
        .replace("yyyy", year)
        .replace("MM", month)
        .replace("dd", day)
        .replace("HH", hours)
        .replace("mm", minutes)
        .replace("ss", seconds)
        .replace("SSS", milliseconds)
}

actual fun getCurrentProcessId(): String {
    // The browser environment is single-process, and OS PID cannot be retrieved for security reasons.
    // Return a fixed identifier.
    return "Wasm"
}

// JS/Wasm is a single-threaded model (Main Thread)
actual fun getCurrentThreadId(): String {
    // JS/Wasm is a single-threaded model (Main Thread)
    return "Main"
}

actual fun appPlatformLogWriter(): LogWriter = CommonWriter()

private fun String.padStart(length: Int, padChar: Char): String {
    if (this.length >= length) return this
    val sb = StringBuilder()
    repeat(length - this.length) {
        sb.append(padChar)
    }
    sb.append(this)
    return sb.toString()
}

// 👇 Define interface for JS native Date object
external class Date() {
    fun getFullYear(): Int
    fun getMonth(): Int
    fun getDate(): Int
    fun getHours(): Int
    fun getMinutes(): Int
    fun getSeconds(): Int
    fun getMilliseconds(): Int
}
