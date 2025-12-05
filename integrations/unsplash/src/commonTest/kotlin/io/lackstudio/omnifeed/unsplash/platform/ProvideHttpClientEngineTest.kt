package io.lackstudio.omnifeed.unsplash.platform

import io.ktor.client.engine.HttpClientEngine

expect fun provideHttpClientEngineTest(): HttpClientEngine
