package io.lackstudio.omnifeed.auth.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import io.lackstudio.omnifeed.auth.data.repository.AuthRepositoryImpl
import io.lackstudio.omnifeed.auth.data.storage.KSafeLocalStorage
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
import io.lackstudio.omnifeed.auth.data.storage.LocalStorage
import io.lackstudio.omnifeed.core.OmniFeedConfig
import org.koin.core.module.Module
import org.koin.dsl.module

actual val omnifeedAuthModule: Module = module {
    includes(authLocalModule)
    single { Firebase.auth }
    single { Firebase.firestore }
    single<AuthRepository> {
        val config = get<OmniFeedConfig>()
        AuthRepositoryImpl(
            firebaseAuth = get(),
            firestore = get(),
            customServices = config.customServices,
            firebaseLocalStorage = get(),
            authManager = get()
        )
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
