package io.lackstudio.omnifeed.unsplash.platform

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO

actual fun provideHttpClientEngineTest(): HttpClientEngine = CIO.create()
