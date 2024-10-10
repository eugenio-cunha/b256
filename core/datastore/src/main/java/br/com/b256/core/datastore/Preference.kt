package br.com.b256.core.datastore

import br.com.b256.core.model.Theme
import kotlinx.coroutines.flow.Flow

interface Preference {
    val theme: Flow<Theme>

    suspend fun setTheme(value: Theme)
}
