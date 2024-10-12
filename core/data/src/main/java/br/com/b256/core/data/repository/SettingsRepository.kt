package br.com.b256.core.data.repository

import br.com.b256.core.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getSettings(): Flow<Settings>

    suspend fun setSettings(value: Settings)
}
