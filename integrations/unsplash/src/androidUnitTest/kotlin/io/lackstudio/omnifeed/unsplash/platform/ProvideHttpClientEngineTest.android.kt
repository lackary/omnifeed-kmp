package io.lackstudio.omnifeed.unsplash.platform

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

actual fun provideHttpClientEngineTest(): HttpClientEngine = Android.create()
