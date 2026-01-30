package io.lackstudio.omnifeed.core.common.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig
import io.lackstudio.omnifeed.core.common.util.isDebuggable

fun createOmniFeedLogger(
    tag: String,
    isDebug: Boolean = isDebuggable,
    logWriter: LogWriter = getPlatformLogWriter(formatter = OmniFeedFormatter())
) = Logger(
    config = StaticConfig(
        minSeverity = if (isDebug) Severity.Verbose else Severity.Error,
        logWriterList = listOf(
            logWriter
        ),
    ),
    tag = tag
)
