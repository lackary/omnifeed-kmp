package io.lackstudio.module.kmp.apiclient.unsplash.platform

import io.ktor.client.engine.HttpClientEngine

expect fun provideHttpClientEngineTest(): HttpClientEngine
