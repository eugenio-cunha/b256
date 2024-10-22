package br.com.b256.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.b256.core.ui.component.Camera
import kotlinx.coroutines.launch


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
    val coroutineScope = rememberCoroutineScope()
    Camera(
        modifier = modifier.fillMaxSize(),
        onCaptureError = {},
        onCaptureSuccess = {
            coroutineScope.launch {
                onShowSnackbar("Grayscale", "fechar")
            }
        },
    )
}
