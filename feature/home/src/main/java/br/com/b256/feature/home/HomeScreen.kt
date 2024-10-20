package br.com.b256.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.b256.core.ui.component.Camera

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {

    Camera(
        modifier = Modifier.fillMaxSize(),
        onCaptureSuccess = {},
        onCaptureError = {},
    )
//    val coroutineScope = rememberCoroutineScope()
//    Box(
//        modifier = modifier
//            .fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Button(
//            onClick = {
//                coroutineScope.launch {
//                    onShowSnackbar("home", "back")
//                }
//            },
//        ) {
//            Text(
//                text = "Home",
//            )
//        }
//    }
}
