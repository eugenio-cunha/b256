package br.com.b256.feature.home

import br.com.b256.core.ui.base.UiIntent

sealed interface HomeIntent : UiIntent {
    data object Load : HomeIntent
    data class ItemClicked(val id: String) : HomeIntent
}
