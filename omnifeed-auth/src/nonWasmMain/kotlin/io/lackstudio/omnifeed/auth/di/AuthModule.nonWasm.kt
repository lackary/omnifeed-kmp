package io.lackstudio.omnifeed.auth.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import io.lackstudio.omnifeed.auth.data.remote.source.AuthRemoteDataSource
import io.lackstudio.omnifeed.auth.data.remote.source.AuthRemoteDataSourceImpl
import io.lackstudio.omnifeed.auth.data.repository.AuthRepositoryImpl
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.auth.domain.usecase.*
import io.lackstudio.omnifeed.core.OmniFeedConfig
import org.koin.core.module.Module
import org.koin.dsl.module

actual val omnifeedAuthModule: Module = module {
    includes(authLocalModule, authRemoteModule)
    
    single { Firebase.auth }
    single { Firebase.firestore }
    
    single<AuthRemoteDataSource> { 
        AuthRemoteDataSourceImpl(
            firebaseAuth = get(),
            firestore = get(),
            authApiService = get(),
            firestoreApiService = get()
        )
    }
    
    single<AuthRepository> {
        val config = get<OmniFeedConfig>()
        AuthRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            customServices = config.customServices,
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
