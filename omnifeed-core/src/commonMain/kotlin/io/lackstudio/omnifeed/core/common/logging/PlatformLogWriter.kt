package io.lackstudio.omnifeed.core.common.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.MessageStringFormatter

expect fun getPlatformLogWriter(formatter: MessageStringFormatter? = null): LogWriter
