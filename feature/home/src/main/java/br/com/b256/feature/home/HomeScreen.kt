package br.com.b256.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.b256.core.ui.component.particles.Particles

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect){
                is HomeEffect.NavigateTo -> {}
                is HomeEffect.ShowSnackbar -> onShowSnackbar(effect.message, null)
            }
        }
    }

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        handleIntent = viewModel::dispatch
    )
}

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    handleIntent: (intent: HomeIntent) -> Unit
) {
    Particles(
        modifier = modifier.clickable {
            handleIntent(HomeIntent.Load)
        }
    )
}
