package io.lackstudio.omnifeed.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.lackstudio.omnifeed.compose.ui.screen.App
import io.lackstudio.omnifeed.compose.ui.screen.AppScreenContent
import io.lackstudio.omnifeed.compose.ui.state.HomeUiState
import io.lackstudio.omnifeed.ui.state.AppUiState

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
    // Prepare fake data (Fake State)
    val fakeUiState = HomeUiState(
        photos = AppUiState.Success(listOf()),
        profile = AppUiState.Idle
    )

    // Render UI directly
    AppScreenContent(
        uiState = fakeUiState,
        authorizeRequestUrl = "https://fake.url",
        onIntent = {} // Empty implementation, because Preview doesn't need to handle clicks
    )
}
