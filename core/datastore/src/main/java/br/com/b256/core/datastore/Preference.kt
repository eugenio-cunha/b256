package br.com.b256.core.datastore

import br.com.b256.core.model.Session
import br.com.b256.core.model.Settings
import br.com.b256.core.model.enums.Theme
import kotlinx.coroutines.flow.Flow

/**
 * Interface para acessar e modificar as preferências do usuário.
 *
 * Esta interface fornece métodos para obter e definir várias preferências,
 * como configurações do aplicativo, informações da sessão do usuário e o tema atual.
 *
 * As preferências são normalmente armazenadas de forma persistente, por exemplo, usando o DataStore.
 * O tipo `Flow` é usado para observar as alterações de preferência de forma reativa.
 */
interface Preference {
    /**
     * Recupera as configurações do aplicativo do usuário como um Flow.
     *
     * Esta função permite observar as alterações nas configurações em tempo real.
     *
     * @return Um [Flow] emitindo objetos [Settings].
     */
    fun getSettings(): Flow<Settings>

    /**
     * Define as configurações do aplicativo.
     *
     * @param value As configurações a serem definidas.
     */
    suspend fun setSettings(value: Settings)

    /**
     * Recupera a sessão atual como um Flow.
     *
     * Esta função permite observar alterações nos dados da sessão.
     *
     * @return Um Flow emitindo a [Session] atual e atualizações subsequentes.
     * O Flow pode emitir `null` se não houver sessão ativa.
     */
    fun getSession(): Flow<Session?>

    /**
     * Define a sessão atual.
     *
     * @param value A [Session] a ser definida.
     */
    suspend fun setSession(value: Session)

    /**
     * Recupera o tema atual como um Flow.
     *
     * Esta função permite observar as alterações no tema da aplicação.
     * Quando o tema é atualizado através de [setTheme], o Flow emitirá o novo valor de [Theme].
     *
     * @return Um [Flow] que emite o [Theme] atual.
     */
    fun getTheme(): Flow<Theme>

    /**
     * Define o tema da aplicação.
     *
     * @param value O [Theme] a ser definido.
     */
    suspend fun setTheme(value: Theme)

    suspend fun clean()
}
