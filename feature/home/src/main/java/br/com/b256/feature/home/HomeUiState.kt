package br.com.b256.feature.home

import br.com.b256.core.model.Settings
import br.com.b256.core.model.enums.Theme
import br.com.b256.core.ui.base.UiState

internal data class HomeUiState(
    val settings: Settings = Settings(
        biometrics = false,
        theme = Theme.FOLLOW_SYSTEM,
    ),
    override val isLoading: Boolean = false,
) : UiState
