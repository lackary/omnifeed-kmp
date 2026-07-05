package io.lackstudio.omnifeed.auth.di

import co.touchlab.kermit.Logger
import io.lackstudio.omnifeed.auth.data.remote.api.FirebaseApiService
import io.lackstudio.omnifeed.auth.data.remote.api.FirebaseApiServiceImpl
import io.lackstudio.omnifeed.auth.platform.firebaseApiKey
import io.lackstudio.omnifeed.core.common.logging.KtorKermitLogger
import io.lackstudio.omnifeed.core.network.KtorClientFactory
import io.lackstudio.omnifeed.core.network.KtorConfig
import io.lackstudio.omnifeed.core.network.provideHttpClientEngine
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.lackstudio.omnifeed.auth.utils.Environment.GOOGLE_CLOUD_API_URL
import io.lackstudio.omnifeed.auth.utils.Environment.GOOGLE_CLOUD_HTTP_HEADER_X_GOOGLE_API_KEY
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val FILENAME_OMNIFEED_AUTH_FIREBASE_TOKEN = "omnifeed_auth_firebase_token"
const val FILENAME_OMNIFEED_AUTH_SERVICE_TOKEN = "omnifeed_auth_service_token"
const val CONFIG_MODULE_NAMESPACE_OMNIFEED_AUTH = "io.omnifeed.auth"

const val TAG_AUTH_KTOR = "AuthKtor"
private val authHttpClient = named("AuthHttpClient")

val authRemoteModule = module {
    single(authHttpClient) {
        val ktorConfig = KtorConfig(
            baseUrl = GOOGLE_CLOUD_API_URL,
        )
        val authLogger = get<Logger>().withTag(TAG_AUTH_KTOR)
        val apiKey = firebaseApiKey ?: throw Exception("Firebase API Key not found")

        KtorClientFactory.createHttpClient(
            engineFactory = provideHttpClientEngine(),
            ktorConfig = ktorConfig,
            logger = KtorKermitLogger(authLogger)
        ) {
            expectSuccess = false
            defaultRequest {
                header(GOOGLE_CLOUD_HTTP_HEADER_X_GOOGLE_API_KEY, apiKey)
            }
        }
    }

    single<FirebaseApiService> {
        FirebaseApiServiceImpl(get(authHttpClient))
    }
}

expect val authLocalModule: Module
expect val omnifeedAuthModule: Module
