package br.com.b256.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.b256.core.model.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : Preference {
    override val theme: Flow<Theme>
        get() = dataStore.data.catch {
            emit(emptyPreferences())
        }.map {
            Theme.from(it[Keys.SETTINGS_THEME].orEmpty())
        }

    override suspend fun setTheme(value: Theme) {
        dataStore.edit {
            it[Keys.SETTINGS_THEME] = value.value
        }
    }

    private object Keys {
        val SETTINGS_THEME = stringPreferencesKey("settings_theme")
    }
}
