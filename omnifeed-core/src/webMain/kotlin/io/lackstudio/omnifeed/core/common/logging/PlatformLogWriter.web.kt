package io.lackstudio.omnifeed.core.common.logging

import co.touchlab.kermit.ConsoleWriter
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.MessageStringFormatter

actual fun getPlatformLogWriter(formatter: MessageStringFormatter?): LogWriter {
    return ConsoleWriter(formatter ?: OmniFeedFormatter())
}
