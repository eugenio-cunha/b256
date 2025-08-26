package br.com.b256.core.model.mapper

import br.com.b256.core.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

/**
 * Mapeia um [Resource] do tipo [T] para um [Resource] do tipo [R] aplicando uma função [transform]
 * ao valor de [Resource.Success]. Se o [Resource] atual for [Resource.Error] ou [Resource.Loading],
 * ele será retornado sem modificação, mantendo o tipo [R] do novo [Resource].
 *
 * @param transform A função a ser aplicada ao valor de [Resource.Success.data].
 * @return Um novo [Resource] do tipo [R].
 */
inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> = when (this) {
    is Resource.Success -> Resource.Success(data = transform(data))
    is Resource.Error -> Resource.Error(message = message)
    is Resource.Loading -> Resource.Loading(isLoading = isLoading)
}

/**
 * Converte um [Flow] do tipo [T] em um [Flow] de [Resource] do tipo [T].
 *
 * Este Flow irá emitir inicialmente [Resource.Loading], seguido por [Resource.Success] com o valor
 * emitido pelo Flow original, ou [Resource.Error] se ocorrer uma exceção durante a coleta do Flow.
 *
 * @return Um [Flow] de [Resource] do tipo [T].
 */
fun <T> Flow<T>.asResource(): Flow<Resource<T>> =
    map<T, Resource<T>> { Resource.Success(data = it) }
        .onStart { emit(value = Resource.Loading(isLoading = true)) }
        .onCompletion { emit(value = Resource.Loading(isLoading = false)) }
        .catch { emit(value = Resource.Error(message = it.localizedMessage)) }
