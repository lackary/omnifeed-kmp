package io.lackstudio.omnifeed.auth.di

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeConfig
import io.lackstudio.omnifeed.auth.AuthManager
import io.lackstudio.omnifeed.auth.data.local.source.AuthLocalDataSource
import io.lackstudio.omnifeed.auth.data.local.source.AuthLocalDataSourceImpl
import io.lackstudio.omnifeed.auth.data.storage.KSafeLocalStorage
import io.lackstudio.omnifeed.auth.data.storage.LocalStorage
import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.auth.domain.usecase.DeleteAccountUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.LinkWithEmailUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.LinkWithGoogleUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.LinkWithCustomServiceUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.UnlinkCustomServiceUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.ObserveUserUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.SignInWithEmailUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.SignInWithGoogleUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.SignInWithCustomServiceUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.SignUpWithEmailUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.SignOutUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.UnlinkProviderUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.UpdatePasswordUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val authLocalModule = module {
    // Firebase Storage
    single<LocalStorage>(named("firebaseStorage")) {
        KSafeLocalStorage(
            KSafe(
                fileName = FILENAME_OMNIFEED_AUTH_FIREBASE_TOKEN,
                config = KSafeConfig(appNamespace = CONFIG_MODULE_NAMESPACE_OMNIFEED_AUTH)
            )
        )
    }

    // Service Storage
    single<LocalStorage>(named("serviceStorage")) {
        KSafeLocalStorage(
            KSafe(
                fileName = FILENAME_OMNIFEED_AUTH_SERVICE_TOKEN,
                config = KSafeConfig(appNamespace = CONFIG_MODULE_NAMESPACE_OMNIFEED_AUTH)
            )
        )
    }

    // AuthLocalDataSource
    single<AuthLocalDataSource> {
        AuthLocalDataSourceImpl(
            firebaseStorage = get(named("firebaseStorage")),
            serviceStorage = get(named("serviceStorage"))
        )
    }
}

actual val omnifeedAuthModule: Module = module {
    includes(authLocalModule)
    single<AuthRepository> { 
        object : AuthRepository {
            override val currentUser: Flow<User?> = flowOf(null)
            override suspend fun signInWithEmail(email: String, password: String): Result<User> = Result.failure(Exception("Not implemented on WasmJs"))
            override suspend fun signUpWithEmail(email: String, password: String, displayName: String?): Result<User> = Result.failure(Exception("Not implemented on WasmJs"))
            override suspend fun signInWithGoogle(idToken: String, accessToken: String?): Result<User> = Result.failure(Exception("Not implemented on WasmJs"))
            override suspend fun signInWithCustomService(serviceName: String, accessToken: String): Result<User> = Result.failure(Exception("Not implemented on WasmJs"))
            override suspend fun linkWithGoogle(idToken: String, accessToken: String?): Result<User> = Result.failure(Exception("Not implemented on WasmJs"))
            override suspend fun linkWithCustomService(serviceName: String, accessToken: String): Result<User> = Result.failure(Exception("Not implemented on WasmJs"))
            override suspend fun unlinkCustomService(serviceName: String): Result<User> = Result.failure(Exception("Not implemented on WasmJs"))
            override suspend fun linkWithEmail(email: String, password: String): Result<User> = Result.failure(Exception("Not implemented on WasmJs"))
            override suspend fun updatePassword(newPassword: String): Result<Unit> = Result.failure(Exception("Not implemented on WasmJs"))
            override suspend fun unlinkProvider(providerId: String): Result<User> = Result.failure(Exception("Not implemented on WasmJs"))
            override suspend fun signOut() {
                val authManager = getOrNull<AuthManager>()
                authManager?.signOut()
            }
            override suspend fun deleteAccount(): Result<Unit> = Result.failure(Exception("Not implemented on WasmJs"))
        }
    }
    factory { SignInWithEmailUseCase(get()) }
    factory { SignUpWithEmailUseCase(get()) }
    factory { SignInWithGoogleUseCase(get()) }
    factory { SignInWithCustomServiceUseCase(get()) }
    factory { LinkWithGoogleUseCase(get()) }
    factory { LinkWithCustomServiceUseCase(get()) }
    factory { UnlinkCustomServiceUseCase(get()) }
    factory { LinkWithEmailUseCase(get()) }
    factory { UpdatePasswordUseCase(get()) }
    factory { UnlinkProviderUseCase(get()) }
    factory { ObserveUserUseCase(get()) }
    factory { SignOutUseCase(get()) }
    factory { DeleteAccountUseCase(get()) }
}
