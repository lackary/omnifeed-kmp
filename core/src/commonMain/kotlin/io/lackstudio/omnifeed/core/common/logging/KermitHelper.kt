package io.lackstudio.omnifeed.core.common.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig
import io.lackstudio.omnifeed.core.common.util.isDebuggable

fun setupKermitLogger(tag: String, logWriter: LogWriter) = Logger(
    config = StaticConfig(
        minSeverity = if (isDebuggable) Severity.Verbose else Severity.Info,
        logWriterList = listOf(
            logWriter
        ),

    ),
    tag = tag
)
