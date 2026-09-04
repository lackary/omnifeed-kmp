package io.lackstudio.omnifeed.auth.di

import co.touchlab.kermit.Logger
import io.lackstudio.omnifeed.auth.data.remote.api.*
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.FIRESTORE_BASE_URL
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.HEADER_X_GOOGLE_API_KEY
import io.lackstudio.omnifeed.auth.data.remote.api.AuthApiConfig.IDENTITY_BASE_URL
import io.lackstudio.omnifeed.auth.platform.firebaseApiKey
import io.lackstudio.omnifeed.core.common.logging.KtorKermitLogger
import io.lackstudio.omnifeed.core.network.KtorClientFactory
import io.lackstudio.omnifeed.core.network.KtorConfig
import io.lackstudio.omnifeed.core.network.provideHttpClientEngine
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val FILENAME_OMNIFEED_AUTH_FIREBASE_TOKEN = "omnifeed_auth_firebase_token"
const val FILENAME_OMNIFEED_AUTH_SERVICE_TOKEN = "omnifeed_auth_service_token"
const val CONFIG_MODULE_NAMESPACE_OMNIFEED_AUTH = "io.lackstudio.omnifeed.auth"

const val TAG_AUTH_KTOR = "AuthKtor"
const val TAG_FIRESTORE_KTOR = "FirestoreKtor"
const val TAG_AUTH_REPOSITORY = "AuthRepository"
const val TAG_AUTH_REMOTE_SOURCE = "AuthRemoteDataSource"
private val namedAuthHttpClient = named("AuthHttpClient")
private val namedFirestoreHttpClient = named("FirestoreHttpClient")
internal val namedUserCacheStorage = named("UserCacheStorage")
internal val namedServiceTokenStorage = named("ServiceTokenStorage")

val authRemoteModule = module {
    single(namedAuthHttpClient) {
        val ktorConfig = KtorConfig(baseUrl = IDENTITY_BASE_URL)
        val authLogger = get<Logger>().withTag(TAG_AUTH_KTOR)
        val apiKey = firebaseApiKey ?: throw Exception("Firebase API Key not found")

        KtorClientFactory.createHttpClient(
            engineFactory = provideHttpClientEngine(),
            ktorConfig = ktorConfig,
            logger = KtorKermitLogger(authLogger)
        ) {
            defaultRequest {
                header(HEADER_X_GOOGLE_API_KEY, apiKey)
            }
        }
    }

    single(namedFirestoreHttpClient) {
        val ktorConfig = KtorConfig(baseUrl = FIRESTORE_BASE_URL)
        val authLogger = get<Logger>().withTag(TAG_FIRESTORE_KTOR)
        val apiKey = firebaseApiKey ?: throw Exception("Firebase API Key not found")

        KtorClientFactory.createHttpClient(
            engineFactory = provideHttpClientEngine(),
            ktorConfig = ktorConfig,
            logger = KtorKermitLogger(authLogger)
        ) {
            defaultRequest {
                header(HEADER_X_GOOGLE_API_KEY, apiKey)
            }
        }
    }

    single<FirebaseAuthApiService> {
        FirebaseAuthApiServiceImpl(get(namedAuthHttpClient))
    }

    single<FirebaseFirestoreApiService> {
        FirebaseFirestoreApiServiceImpl(get(namedFirestoreHttpClient))
    }
}

expect val authLocalModule: Module
expect val omnifeedAuthModule: Module
