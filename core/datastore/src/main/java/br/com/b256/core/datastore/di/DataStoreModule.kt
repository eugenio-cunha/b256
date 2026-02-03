package br.com.b256.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import br.com.b256.core.common.B256Dispatchers.IO
import br.com.b256.core.common.Dispatcher
import br.com.b256.core.common.di.ApplicationScope
import br.com.b256.core.datastore.CookieStore
import br.com.b256.core.datastore.CookieStoreImpl
import br.com.b256.core.datastore.Preference
import br.com.b256.core.datastore.PreferenceManager
import br.com.b256.core.datastore.di.DataStoreModule.USER_COOKIES
import br.com.b256.core.datastore.serializer.StringListMapSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

/**
 * Módulo Dagger Hilt que fornece instâncias do DataStore para as configurações do aplicativo.
 *
 * Ele utiliza o Proto DataStore para persistência de dados com segurança de tipo.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    internal fun providesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            migrations = listOf(
                SharedPreferencesMigration(
                    context = context,
                    sharedPreferencesName = USER_PREFERENCES,
                ),
            ),
            produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES) },
        )

    @Provides
    @Singleton
    internal fun providesPreference(dataStore: DataStore<Preferences>): Preference {
        return PreferenceManager(dataStore = dataStore)
    }

    /**
     * Fornece uma instância singleton de [DataStore] para [Preferences].
     *
     * Este DataStore é usado para armazenar cookies do usuário. Ele usa [PreferenceDataStoreFactory]
     * para criar uma instância que armazena dados em um arquivo chamado [USER_COOKIES].
     *
     * @param context O contexto da aplicação, usado para obter o caminho do arquivo para o DataStore.
     * @return Uma instância de [DataStore] para [Preferences].
     */
    @Provides
    @Singleton
    fun providesCookieDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        serializer: StringListMapSerializer,
    ): CookieStore = CookieStoreImpl(
        dataStore = DataStoreFactory.create(
            serializer = serializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
        ) {
            context.dataStoreFile(USER_COOKIES)
        },
    )

    private const val USER_COOKIES = "user_cookies"
    private const val USER_PREFERENCES = "user_preferences"
}
