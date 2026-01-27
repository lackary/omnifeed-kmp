package io.lackstudio.omnifeed.core.common.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.MessageStringFormatter
import co.touchlab.kermit.NSLogWriter

actual fun getPlatformLogWriter(formatter: MessageStringFormatter): LogWriter {
    return NSLogWriter(formatter)
}
