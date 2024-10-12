package br.com.b256.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
internal fun HomeScreen (
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        modifier = modifier,
        uiState = uiState
    )
}

@Composable
internal fun HomeScreen (
    modifier: Modifier = Modifier,
    uiState: HomeUiState
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Text(
            text = "Home"
        )
    }
}
