package io.lackstudio.omnifeed.auth.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import io.lackstudio.omnifeed.auth.data.repository.AuthRepositoryImpl
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.auth.domain.usecase.SignInWithEmailUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.SignInWithGoogleUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.SignUpWithEmailUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val omnifeedAuthModule: Module = module {
    single { Firebase.auth }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    factory { SignInWithEmailUseCase(get()) }
    factory { SignUpWithEmailUseCase(get()) }
    factory { SignInWithGoogleUseCase(get()) }
}
