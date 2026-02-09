package br.com.b256.feature.home

import br.com.b256.core.ui.base.BaseViewModel
import br.com.core.b256.domain.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
) : BaseViewModel<HomeIntent, HomeUiState, HomeEffect>(HomeUiState()) {

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.ItemClicked -> {}
            HomeIntent.Load -> loadData()
        }
    }

    private suspend fun loadData() {
        sendEffect(HomeEffect.ShowSnackbar("Dados carregados"))
//        getSettingsUseCase().collect { settings ->
//
//            reduce { HomeUiState(settings = settings, isLoading = false) }
//        }
    }
}
