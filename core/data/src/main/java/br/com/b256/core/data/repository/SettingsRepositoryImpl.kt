package br.com.b256.core.data.repository

import br.com.b256.core.datastore.Preference
import br.com.b256.core.model.Settings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val preference: Preference
): SettingsRepository{
    override suspend fun getSettings(): Flow<Settings> {
        return preference.getSettings()
    }

    override suspend fun setSettings(value: Settings) {
        preference.setSettings(value = value)
    }
}
