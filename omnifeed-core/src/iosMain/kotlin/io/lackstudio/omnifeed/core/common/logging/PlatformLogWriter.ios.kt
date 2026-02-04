package io.lackstudio.omnifeed.core.common.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.MessageStringFormatter
import co.touchlab.kermit.XcodeSeverityWriter

actual fun getPlatformLogWriter(formatter: MessageStringFormatter?): LogWriter {
    return XcodeSeverityWriter( formatter ?: OmniFeedFormatter())
}
