package br.com.b256.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.b256.core.ui.component.rememberCamera
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
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val camera = rememberCamera(
            context = LocalContext.current,
            onPicture = {
                coroutineScope.launch {
                    onShowSnackbar(it.toString(), "back")
                }
            },
            onPermissionDenied = {},
        )

        LaunchedEffect(Unit) {
            camera.launch()

        }

//        Button(
//            onClick = {
//
//            },
//        ) {
//            Text(
//                text = "Home",
//            )
//        }
    }
}
