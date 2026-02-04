package io.lackstudio.omnifeed.core.common.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig

fun createOmniFeedLogger(
    tag: String,
    isDebug: Boolean = false,
    logWriter: LogWriter = getPlatformLogWriter()
) = Logger(
    config = StaticConfig(
        minSeverity = if (isDebug) Severity.Verbose else Severity.Error,
        logWriterList = listOf(
            logWriter
        ),
    ),
    tag = tag
)
