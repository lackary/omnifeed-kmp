package io.lackstudio.omnifeed.shared.ui.screen

import androidx.compose.ui.test.*
import io.lackstudio.omnifeed.shared.di.viewModelModule
import io.lackstudio.omnifeed.shared.platform.getUnsplashAccessKey
import io.lackstudio.omnifeed.core.OmniFeedConfig
import io.lackstudio.omnifeed.core.UnsplashConfig
import io.lackstudio.omnifeed.core.di.coreModule
import io.lackstudio.omnifeed.unsplash.di.unsplashModule
import io.lackstudio.omnifeed.unsplash.utils.Environment.AUTH_SCHEME_PUBLIC
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * For the record
 * Currently, you cannot run common Compose Multiplatform tests using android (local) test configurations,
 * so gutter icons in Android Studio, for example, won't be helpful.
 * reference by https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html#d6lghh_55
 * 2025-9-22
 */

class AppTest : KoinTest {

    @BeforeTest
    fun setupKoin() {
        val config = OmniFeedConfig(
            appLogger = null,
            unsplash = UnsplashConfig(
                tokenType = "Client-ID",
                token = getUnsplashAccessKey()
            )
        )
        startKoin {
            modules(
                listOf(
                    coreModule(config),
                    unsplashModule(AUTH_SCHEME_PUBLIC, getUnsplashAccessKey()),
                    viewModelModule
                )
            )
        }
    }

    @AfterTest
    fun tearDownKoin() {
        stopKoin()
    }

    @ExperimentalTestApi
    @Test
    fun testAppScreen() = runComposeUiTest {
        setContent {
            App()
        }

        onNodeWithTag("display_button").assertExists()
        onNodeWithText("Show Me Example!!").assertDoesNotExist()

        onNodeWithTag("display_button").performClick()

        onNodeWithTag("display_text").assertExists()
        onNodeWithText("Show Me Example!!").assertIsDisplayed()
        onNodeWithTag("multiplatform_logo").assertIsDisplayed()

    }

}
