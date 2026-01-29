package io.lackstudio.omnifeed.core.common.logging

import co.touchlab.kermit.Message
import co.touchlab.kermit.MessageStringFormatter
import co.touchlab.kermit.Severity
import co.touchlab.kermit.Tag

class OmniFeedFormatter : MessageStringFormatter {
    override fun formatMessage(severity: Severity?, tag: Tag?, message: Message): String {
        val severityStr = severity?.name?.firstOrNull()?.let { "[$it] " } ?: ""
        val tagStr = (tag?.tag ?: "OmniFeed").let { "[$it] " }

        // format example: [I][OmniFeed] "$message"
        return "$severityStr$tagStr${message.message}".trim()
    }
}
