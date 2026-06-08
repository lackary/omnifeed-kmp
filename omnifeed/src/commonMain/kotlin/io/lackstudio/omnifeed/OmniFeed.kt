package io.lackstudio.omnifeed

import io.lackstudio.omnifeed.auth.di.omnifeedAuthModule
import io.lackstudio.omnifeed.auth.domain.usecase.DeleteAccountUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.LinkWithEmailUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.LinkWithGoogleUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.ObserveUserUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.SignInWithEmailUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.SignInWithGoogleUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.SignUpWithEmailUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.SignOutUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.UnlinkProviderUseCase
import io.lackstudio.omnifeed.auth.domain.usecase.UpdatePasswordUseCase
import io.lackstudio.omnifeed.core.OmniFeedConfig
import io.lackstudio.omnifeed.core.di.coreModule
import io.lackstudio.omnifeed.core.network.oauth.AccessTokenProvider
import io.lackstudio.omnifeed.unsplash.di.unsplashModule
import io.lackstudio.omnifeed.unsplash.domain.usecase.ExchangeOAuthUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetCollectionPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetMeUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetPhotoUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetCollectionsUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetTopicPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetTopicUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetTopicsUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserCollectionsUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserLikedPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.GetUserPublicProfileUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.SearchCollectionsUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.SearchPhotosUseCase
import io.lackstudio.omnifeed.unsplash.domain.usecase.SearchUsersUseCase
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication

object OmniFeed {
    private var internalKoinApp: KoinApplication? = null

    /**
     * SDK initialization entry point.
     * Call this function before using SDK features.
     */
    fun initialize(config: OmniFeedConfig) {
        if (internalKoinApp != null) return

        // Create an isolated Koin Application
        internalKoinApp = koinApplication {
            modules(
                // Load all internal modules here
                coreModule(config),
                unsplashModule(
                    tokenType = config.unsplash.tokenType, // or passed from config
                    token = config.unsplash.token // token strategy depends on implementation
                ),
                omnifeedAuthModule
            )
        }
    }

    // --- Internal Helpers (Not exposed to users) ---
    private inline fun <reified T : Any> getDependency(): T {
        return internalKoinApp?.koin?.get()
            ?: error("OmniFeed is not initialized! Call OmniFeed.initialize() first.")
    }

    // --- Public APIs (Bridge Components) ---
    // These are the components needed by ViewModels, exposed through the SDK.

    val getPhotosUseCase: GetPhotosUseCase get() = getDependency()

    val getPhotoUseCase: GetPhotoUseCase get() = getDependency()

    val getCollectsUseCase: GetCollectionsUseCase get() = getDependency()

    val getCollectionUseCase: GetCollectionsUseCase get() = getDependency()

    val getCollectionPhotosUseCase: GetCollectionPhotosUseCase get() = getDependency()

    val getCollectionRelatedCollectionsUseCase: GetCollectionPhotosUseCase get() = getDependency()

    val getTopicsUseCase: GetTopicsUseCase get() = getDependency()

    val getTopicUseCase: GetTopicUseCase get() = getDependency()

    val getTopicPhotosUseCase: GetTopicPhotosUseCase get() = getDependency()

    val getUserPublicProfileUseCase: GetUserPublicProfileUseCase get() = getDependency()

    val getUserPhotosUseCase: GetUserPhotosUseCase get() = getDependency()

    val getUserCollectionsUseCase: GetUserCollectionsUseCase get() = getDependency()

    val getUserLikedPhotosUseCase: GetUserLikedPhotosUseCase get() = getDependency()

    val getSearchPhotosUseCase: SearchPhotosUseCase get() = getDependency()

    val getSearchCollectionsUseCase: SearchCollectionsUseCase get() = getDependency()

    val getSearchUsersUseCase: SearchUsersUseCase get() = getDependency()

    val exchangeOAuthUseCase: ExchangeOAuthUseCase get() = getDependency()

    val getMeUseCase: GetMeUseCase get() = getDependency()

    // --- Auth APIs ---
    val signInWithEmailUseCase: SignInWithEmailUseCase get() = getDependency()
    val signUpWithEmailUseCase: SignUpWithEmailUseCase get() = getDependency()
    val signInWithGoogleUseCase: SignInWithGoogleUseCase get() = getDependency()
    val linkWithGoogleUseCase: LinkWithGoogleUseCase get() = getDependency()
    val linkWithEmailUseCase: LinkWithEmailUseCase get() = getDependency()
    val updatePasswordUseCase: UpdatePasswordUseCase get() = getDependency()
    val unlinkProviderUseCase: UnlinkProviderUseCase get() = getDependency()
    val observeUserUseCase: ObserveUserUseCase get() = getDependency()
    val signOutUseCase: SignOutUseCase get() = getDependency()
    val deleteAccountUseCase: DeleteAccountUseCase get() = getDependency()

    // This is typically a Singleton, ensuring the App and SDK share the same token state.
    val accessTokenProvider: AccessTokenProvider get() = getDependency()

//    // --- Internal Helpers (Not exposed to users) ---
//    private fun <T : Any> get(clazz: KClass<T>): T {
//        val app = internalKoinApp ?: error("OmniFeed not initialized! Call OmniFeed.initialize() first.")
//        return app.koin.get(clazz)
//    }
//
//    // --- Public APIs ---
//    // User call: OmniFeed.searchPhotosUseCase.invoke(...)
//    val searchPhotosUseCase: SearchPhotosUseCase get() = get(SearchPhotosUseCase::class)
//
//
//    // Repositories can also be exposed if needed
//    // val unsplashRepository: UnsplashRepository get() = get(UnsplashRepository::class)
}
