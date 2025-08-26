package br.com.b256.core.data.repository

import br.com.b256.core.model.Settings
import br.com.b256.core.model.enums.Theme
import kotlinx.coroutines.flow.Flow

/**
 * Repositório para acessar e modificar as configurações do aplicativo.
 */
interface SettingsRepository {
    /**
     * Recupera as configurações da aplicação como um Flow.
     *
     * Esta função permite observar mudanças nas configurações da aplicação.
     * Quando as configurações são atualizadas, o Flow emitirá o novo objeto [Settings].
     *
     * @return Um [Flow] que emite objetos [Settings].
     */
    fun getSettings(): Flow<Settings>

    /**
     * Define as configurações do aplicativo.
     *
     * @param value O objeto [Settings] a ser salvo.
     */
    suspend fun setSettings(value: Settings)

    /**
     * Recupera o tema atual como um Flow.
     *
     * Esta função permite observar mudanças na configuração do tema.
     *
     * @return Um Flow emitindo o [Theme] atual.
     */
    fun getTheme(): Flow<Theme>

    /**
     * Define o tema da aplicação.
     *
     * @param value O [Theme] a ser definido.
     */
    suspend fun setTheme(value: Theme)
}
