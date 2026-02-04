package io.lackstudio.omnifeed.core.common.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Message
import co.touchlab.kermit.MessageStringFormatter
import co.touchlab.kermit.Severity
import co.touchlab.kermit.Tag
import org.slf4j.LoggerFactory

class Slf4jLogWriter(
    private val formatter: MessageStringFormatter
) : LogWriter() {
    override fun log(
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?
    ) {
        val logger = LoggerFactory.getLogger(tag)

        val formattedMessage = formatter.formatMessage(
            severity = severity,
            tag = Tag(tag),
            message = Message(message)
        )

        when (severity) {
            Severity.Verbose -> logger.trace(formattedMessage, throwable)
            Severity.Debug -> logger.debug(formattedMessage, throwable)
            Severity.Info -> logger.info(formattedMessage, throwable)
            Severity.Warn -> logger.warn(formattedMessage, throwable)
            Severity.Error -> logger.error(formattedMessage, throwable)
            Severity.Assert -> logger.error(formattedMessage, throwable)
        }
    }
}
