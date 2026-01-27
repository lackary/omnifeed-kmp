package io.lackstudio.omnifeed.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.lackstudio.omnifeed.compose.di.viewModelModule
import io.lackstudio.omnifeed.compose.platform.getUnsplashAccessKey
import io.lackstudio.omnifeed.compose.ui.screen.App
import io.lackstudio.omnifeed.core.di.coreModule
import io.lackstudio.omnifeed.unsplash.di.unsplashModule
import io.lackstudio.omnifeed.unsplash.utils.Environment as UnsplashEnvironment
import org.koin.compose.KoinApplication

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    KoinApplication(application = {
        // Initialize Koin for the Compose Preview environment.
        // This ensures that dependencies required by the App composable (like ViewModels or Loggers)
        // are available during rendering.
        modules(
            listOf(
                coreModule(),
                unsplashModule(
                    tokenType = UnsplashEnvironment.AUTH_SCHEME_PUBLIC,
                    token = getUnsplashAccessKey()
                ),
                viewModelModule
            )
        )
    }) {
        App()
    }
}
