package io.lackstudio.omnifeed.auth.di

import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.auth.domain.usecase.SignInWithEmailUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.SignInWithGoogleUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.SignUpWithEmailUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.module.Module
import org.koin.dsl.module

actual val omnifeedAuthModule: Module = module {
    single<AuthRepository> { 
        object : AuthRepository {
            override val currentUser: Flow<User?> = flowOf(null)
            override suspend fun signInWithEmail(email: String, password: String): Result<User> = Result.failure(Exception("Not implemented on WasmJs"))
            override suspend fun signUpWithEmail(email: String, password: String, displayName: String?): Result<User> = Result.failure(Exception("Not implemented on WasmJs"))
            override suspend fun signInWithGoogle(idToken: String, accessToken: String?): Result<User> = Result.failure(Exception("Not implemented on WasmJs"))
            override suspend fun signOut() {}
        }
    }
    factory { SignInWithEmailUseCase(get()) }
    factory { SignUpWithEmailUseCase(get()) }
    factory { SignInWithGoogleUseCase(get()) }
}
