package br.com.b256.core.datastore

import br.com.b256.core.model.Settings
import kotlinx.coroutines.flow.Flow

interface Preference {
    suspend fun setSettings(value: Settings)

    suspend fun getSettings(): Flow<Settings>
}
