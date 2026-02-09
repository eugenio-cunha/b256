package br.com.b256.feature.home

import br.com.b256.core.ui.base.UiEffect

sealed interface HomeEffect : UiEffect {
    data class ShowSnackbar(val message: String) : HomeEffect
    data class NavigateTo(val route: String) : HomeEffect
}
