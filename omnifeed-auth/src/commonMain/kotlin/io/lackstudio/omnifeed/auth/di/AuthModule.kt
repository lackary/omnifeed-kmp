package io.lackstudio.omnifeed.auth.di

import org.koin.core.module.Module

const val FILENAME_OMNIFEED_AUTH_FIREBASE_TOKEN = "omnifeed_auth_firebase_token"
const val FILENAME_OMNIFEED_AUTH_SERVICE_TOKEN = "omnifeed_auth_service_token"
const val CONFIG_MODULE_NAMESPACE_OMNIFEED_AUTH = "io.omnifeed.auth"

expect val authLocalModule: Module
expect val omnifeedAuthModule: Module
