package br.com.b256.core.model

/**
 * Uma interface selada que representa um recurso que pode estar em um dos três estados:
 * [Success], [Error] ou [Loading].
 *
 * @param T O tipo de dados mantido pelo recurso no estado de [Success].
 */
sealed interface Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val message: String) : Resource<Nothing>
    data class Loading(val isLoading: Boolean) : Resource<Nothing>
}
