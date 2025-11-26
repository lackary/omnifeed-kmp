package io.lackstudio.module.kmp.apiclient.unsplash.platform

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO

actual fun provideHttpClientEngineTest(): HttpClientEngine = CIO.create()
