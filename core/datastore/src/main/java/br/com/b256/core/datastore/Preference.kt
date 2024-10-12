package br.com.b256.core.datastore

import br.com.b256.core.model.Settings
import kotlinx.coroutines.flow.Flow

interface Preference {
    fun getSettings(): Flow<Settings>

    suspend fun setSettings(value: Settings)
}
