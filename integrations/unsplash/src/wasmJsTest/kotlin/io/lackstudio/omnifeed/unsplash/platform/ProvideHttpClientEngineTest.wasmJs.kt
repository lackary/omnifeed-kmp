package io.lackstudio.omnifeed.unsplash.platform

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js

actual fun provideHttpClientEngineTest(): HttpClientEngine = Js.create()
