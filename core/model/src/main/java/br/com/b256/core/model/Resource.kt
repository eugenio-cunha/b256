package br.com.b256.core.model

/**
 * Uma interface selada que representa um recurso que pode estar em um dos três estados:
 * [Success], [Failure] ou [Loading].
 *
 * @param T O tipo de dados mantido pelo recurso no estado de [Success].
 */
sealed interface Resource<out T> {
    /**
     * Representa um estado de sucesso de uma operação de recurso.
     *
     * @param T O tipo de dados mantido pelo recurso.
     * @property data Os dados associados ao sucesso. Pode ser nulo se a operação bem-sucedida não produzir dados.
     */
    data class Success<T>(val data: T?) : Resource<T>

    /**
     * Representa um estado de falha de uma operação de recurso.
     *
     * @param message Uma mensagem descrevendo a falha.
     */
    data class Failure(val message: String) : Resource<Nothing>

    /**
     * Representa o estado de carregamento de um recurso.
     *
     * @property isLoading Indica se o recurso está atualmente carregando (true) ou não (false).
     */
    data class Loading(val isLoading: Boolean) : Resource<Nothing>
}
