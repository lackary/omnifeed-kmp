package io.lackstudio.omnifeed.shared.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.lackstudio.omnifeed.shared.generated.resources.Res
import io.lackstudio.omnifeed.shared.generated.resources.compose_multiplatform
import io.lackstudio.omnifeed.shared.platform.getUnsplashAccessKey
import io.lackstudio.omnifeed.shared.ui.event.HomeUiEvent
import io.lackstudio.omnifeed.shared.ui.intent.HomeUiIntent
import io.lackstudio.omnifeed.shared.ui.state.HomeUiState
import io.lackstudio.omnifeed.shared.ui.viewmodel.AppViewModel
import io.lackstudio.omnifeed.shared.utils.Environment
import io.lackstudio.omnifeed.core.network.extension.hrefWithHost
import io.lackstudio.omnifeed.ui.state.AppUiState
import io.lackstudio.omnifeed.unsplash.data.remote.model.request.AuthorizeRequest as UnsplashAuthorizeRequest
import io.lackstudio.omnifeed.unsplash.utils.Environment as UnsplashEnvironment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

/**
 * 1. Stateful Composable (The Container)
 * Responsible for dependency injection, state collection, and logic handling.
 * The actual UI rendering is delegated to the stateless [AppScreenContent].
 */
@Composable
fun App() {
    // 1. Dependency Injection
    val baseLogger: Logger = koinInject()
    val appViewModel: AppViewModel = koinInject()
    val client: HttpClient = koinInject()

    // 2. State Collection
    val uiState by appViewModel.uiState.collectAsState()

    // 3. Logic & Event Handling
    // Prepare the special logic to be passed to the UI here

    // Prepare OAuth URL (Pure logic, independent of UI)
    val authRequest = remember {
        UnsplashAuthorizeRequest(
            clientId = getUnsplashAccessKey(),
            redirectUri = Environment.AUTH_REDIRECT_URL,
            responseType = "code",
            scope = "public"
        )
    }

    // Use the client to generate the URL here
    val authorizeRequestUrl = remember(client) {
        client.hrefWithHost(
            hostname = UnsplashEnvironment.HOST_NAME,
            resource = authRequest
        )
    }

    val logger = remember { baseLogger.withTag("AppKt") }

    // 4. Pass Everything to Stateless Content
    AppScreenContent(
        uiState = uiState,
        eventFlow = appViewModel.eventsFlow, // Pass the one-time events Flow
        authorizeRequestUrl = authorizeRequestUrl,
        logger = logger,
        onIntent = { intent -> appViewModel.processIntent(intent) }
    )
}

/**
 * Stateless Composable (The UI)
 * Pure UI component. Completely unaware of ViewModel and Koin.
 * Only receives State (data) and Lambdas (actions).
 */
@Composable
fun AppScreenContent(
    uiState: HomeUiState,
    eventFlow: Flow<HomeUiEvent> = emptyFlow(), // Default to empty Flow for easy Preview
    authorizeRequestUrl: String = "",
    logger: Logger? = null, // Optional Logger for debugging UI
    onIntent: (HomeUiIntent) -> Unit
) {
    var authUrlToShow: String? by remember { mutableStateOf(null) }

    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = { showContent = !showContent },
                modifier = Modifier.testTag("display_button")
            ) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.compose_multiplatform),
                        contentDescription = null,
                        modifier = Modifier.testTag("multiplatform_logo")
                    )
                    Text(
                        text = "Show Me Example!!",
                        modifier = Modifier.testTag("display_text")
                    )
                }
            }

            val coroutineScope = rememberCoroutineScope()
            ButtonSignIn(
                Modifier.testTag("ButtonGoogleSignIn")
            ) {
                coroutineScope.launch { }
            }

            //************************** My Login Buttons *************
            HorizontalDivider(
                Modifier.fillMaxWidth().padding(16.dp),
                DividerDefaults.Thickness,
                DividerDefaults.color
            )

            // Button to open the Bottom Sheet
            Button(onClick = { authUrlToShow = authorizeRequestUrl }) {
                Text("Open WebView")
            }

            logger?.i { "authorizeRequestUrl = $authorizeRequestUrl" }
        }
    }
}

@Composable
fun ButtonSignIn(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = "Google Sign In")
    }
}

/**
 * Directly preview [AppScreenContent].
 */
@Preview
@Composable
fun AppPreview() {
    // Prepare fake data (Fake State)
    val fakeUiState = HomeUiState(
        photos = AppUiState.Success(listOf()), // Can include some fake photo data
        profile = AppUiState.Idle
    )

    // Render UI directly
    AppScreenContent(
        uiState = fakeUiState,
        authorizeRequestUrl = "https://fake.url",
        onIntent = {} // Empty implementation as Preview doesn't need to handle clicks
    )
}
