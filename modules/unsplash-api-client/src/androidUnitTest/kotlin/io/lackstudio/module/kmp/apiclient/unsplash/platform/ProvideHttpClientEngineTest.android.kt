package io.lackstudio.module.kmp.apiclient.unsplash.platform

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

actual fun provideHttpClientEngineTest(): HttpClientEngine = Android.create()
