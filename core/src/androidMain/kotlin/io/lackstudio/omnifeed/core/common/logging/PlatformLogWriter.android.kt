package io.lackstudio.omnifeed.core.common.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.LogcatWriter
import co.touchlab.kermit.MessageStringFormatter

actual fun getPlatformLogWriter(formatter: MessageStringFormatter): LogWriter {

    return LogcatWriter(messageStringFormatter = object : MessageStringFormatter {
        override fun formatMessage(
            severity: co.touchlab.kermit.Severity?,
            tag: co.touchlab.kermit.Tag?,
            message: co.touchlab.kermit.Message
        ): String {
            // Android only needs to return the original message, no prefix required.
            return message.message
        }
    })
}
