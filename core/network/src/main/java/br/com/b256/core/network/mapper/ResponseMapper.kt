package br.com.b256.core.network.mapper

import br.com.b256.core.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import okhttp3.Headers
import retrofit2.Response

/**
 * Extrai a mensagem de erro de uma [Response] do Retrofit.
 *
 * Esta função de extensão tenta obter a mensagem de erro da resposta de uma requisição de rede
 * que falhou. A ordem de precedência para obter a mensagem é:
 * 1. O conteúdo do [errorBody] da resposta, se não estiver vazio.
 * 2. A mensagem padrão da resposta ([Response.message]), caso o [errorBody] não esteja disponível ou esteja em branco.
 *
 * @param T O tipo do corpo esperado na resposta bem-sucedida (não utilizado diretamente, mas necessário para a extensão).
 * @return Uma [String] contendo a mensagem de erro.
 */
private fun <T> Response<T>.errorMessage(): String =
    errorBody()
        ?.string()
        ?.takeIf(String::isNotBlank)
        ?: message()

/**
 * Executa o mapeamento de uma [Response] para [Resource], centralizando
 * o tratamento de sucesso, erro HTTP e exceções.
 *
 * Comportamento:
 * - Se a resposta for bem-sucedida ([Response.isSuccessful]):
 *     - Aplica a função [mapper] ao corpo da resposta, se não for nulo.
 *     - Retorna [Resource.Success], permitindo que `data` seja nulo caso o corpo esteja ausente.
 * - Se a resposta não for bem-sucedida:
 *     - Retorna [Resource.Failure] com a mensagem extraída da resposta.
 * - Se ocorrer qualquer exceção durante o processamento:
 *     - Retorna [Resource.Failure] com uma mensagem de erro inesperado.
 *
 * @param R O tipo do corpo da resposta original.
 * @param O O tipo do dado após o mapeamento.
 * @param mapper Função responsável por transformar o corpo da resposta.
 */
private inline fun <R, O> Response<R>.mapToResource(
    mapper: (R) -> O,
): Resource<O> =
    try {
        if (isSuccessful) {
            Resource.Success(data = body()?.let(mapper))
        } else {
            Resource.Failure(message = errorMessage())
        }
    } catch (e: Exception) {
        Resource.Failure(message = "$UNEXPECTED_ERROR_MESSAGE: ${e.localizedMessage.orEmpty()}")
    }

/**
 * Aplica o ciclo padrão de loading a um [Flow] de [Resource].
 *
 * Este operador:
 * - Emite [Resource.Loading] com `isLoading = true` no início da coleta.
 * - Captura exceções lançadas durante a execução do fluxo e emite [Resource.Failure].
 * - Emite [Resource.Loading] com `isLoading = false` ao final da execução, independentemente do resultado.
 *
 * Observação: esta função **não cria estados de sucesso ou erro HTTP**,
 * apenas complementa um fluxo existente com estados de loading e tratamento
 * de exceções em nível de Flow.
 */
private fun <T> Flow<Resource<T>>.withLoading(): Flow<Resource<T>> =
    onStart { emit(Resource.Loading(isLoading = true)) }
        .catch { e ->
            emit(
                Resource.Failure(
                    message = "$UNEXPECTED_ERROR_MESSAGE: ${e.localizedMessage.orEmpty()}",
                ),
            )
        }
        .onCompletion { emit(Resource.Loading(isLoading = false)) }

/**
 * Mapeia uma [Response] para um [Flow] de objetos [Resource].
 *
 * Esta função lida com os diferentes estados da requisição de rede:
 * - Emite [Resource.Loading] com `isLoading = true` no início do fluxo.
 * - Se a resposta for bem-sucedida ([Response.isSuccessful] for verdadeiro):
 *     - Aplica a função [mapper] ao corpo da resposta.
 *     - Emite [Resource.Success] com os dados mapeados.
 * - Se a resposta não for bem-sucedida:
 *     - Emite [Resource.Failure] com a mensagem de erro da resposta.
 * - Se ocorrer alguma exceção durante o processo:
 *     - Emite [Resource.Failure] com uma mensagem de erro genérica incluindo a mensagem localizada da exceção.
 * - Finalmente, emite [Resource.Loading] com `isLoading = false` para indicar que a requisição foi concluída.
 *
 * @param R O tipo do corpo da resposta original.
 * @param O O tipo dos dados após o mapeamento.
 * @param mapper Uma função lambda para transformar o corpo da resposta do tipo [R] para o tipo [O].
 * @return Um [Flow] que emite objetos [Resource] representando o estado da operação de rede.
 */
internal inline fun <R, O> Response<R>.asResourceFlow(crossinline mapper: (R) -> O): Flow<Resource<O>> =
    flow {
        emit(mapToResource { mapper(it) })
    }.withLoading()

/**
 * Mapeia uma [Response] para um [Flow] de objetos [Resource], utilizando os cabeçalhos da resposta no mapeamento.
 *
 * Esta função lida com os diferentes estados da requisição de rede:
 * - Emite [Resource.Loading] com `isLoading = true` no início do fluxo.
 * - Se a resposta for bem-sucedida ([Response.isSuccessful] for verdadeiro):
 *     - Aplica a função [mapper] aos cabeçalhos ([Headers]) e ao corpo da resposta.
 *     - Emite [Resource.Success] com os dados mapeados.
 * - Se a resposta não for bem-sucedida:
 *     - Emite [Resource.Failure] com a mensagem de erro da resposta.
 * - Se ocorrer alguma exceção durante o processo:
 *     - Emite [Resource.Failure] com uma mensagem de erro genérica incluindo a mensagem localizada da exceção.
 * - Finalmente, emite [Resource.Loading] com `isLoading = false` para indicar que a requisição foi concluída.
 *
 * @param R O tipo do corpo da resposta original.
 * @param O O tipo dos dados após o mapeamento.
 * @param mapper Uma função lambda para transformar os cabeçalhos ([Headers]) e o corpo da resposta do tipo [R] para o tipo [O].
 * @return Um [Flow] que emite objetos [Resource] representando o estado da operação de rede.
 */
internal inline fun <R, O> Response<R>.asResourceFlow(crossinline mapper: (Headers, R) -> O): Flow<Resource<O>> =
    flow {
        emit(
            mapToResource { body ->
                mapper(headers(), body)
            },
        )
    }.withLoading()

/**
 * Converte uma [Response] do Retrofit em um [Flow] de [Resource].
 *
 * Esta função lida com os diferentes estados de uma requisição de rede:
 * - Emite [Resource.Loading] com `isLoading = true` no início do fluxo.
 * - Se a resposta for bem-sucedida (HTTP 2xx), emite [Resource.Success] com o corpo da resposta.
 * - Se a resposta não for bem-sucedida, emite [Resource.Failure] com a mensagem de erro da resposta.
 * - Se ocorrer qualquer exceção durante o processo, emite [Resource.Failure] com uma mensagem de erro genérica.
 * - Emite [Resource.Loading] com `isLoading = false` no bloco `finally` para garantir que o estado de carregamento seja sempre redefinido.
 *
 * @return Um [Flow] que emite estados [Resource] representando o ciclo de vida da requisição de rede.
 */
internal fun <T> Response<T>.asResourceFlow(): Flow<Resource<T>> =
    flow {
        emit(mapToResource { it })
    }.withLoading()

/**
 * Mapeia uma [Response] para um objeto [Resource].
 *
 * Esta função lida com os diferentes estados da requisição de rede:
 * - Se a resposta for bem-sucedida ([Response.isSuccessful] for verdadeiro):
 *     - Aplica a função [mapper] ao corpo da resposta.
 *     - Retorna [Resource.Success] com os dados mapeados.
 * - Se a resposta não for bem-sucedida:
 *     - Retorna [Resource.Failure] com a mensagem de erro da resposta.
 * - Se ocorrer alguma exceção durante o processo:
 *     - Retorna [Resource.Failure] com uma mensagem de erro genérica incluindo a mensagem localizada da exceção.
 *
 * @param R O tipo do corpo da resposta original.
 * @param O O tipo dos dados após o mapeamento.
 * @param mapper Uma função lambda para transformar o corpo da resposta do tipo [R] para o tipo [O].
 * @return Um objeto [Resource] representando o resultado da operação de rede.
 */
internal inline fun <R, O> Response<R>.asResource(crossinline mapper: (R) -> O): Resource<O> =
    mapToResource { mapper(it) }

/**
 * Converte uma [Response] do Retrofit em um objeto [Resource].
 *
 * Esta função trata os diferentes resultados de uma chamada de rede:
 * - Se a resposta for bem-sucedida ([Response.isSuccessful] for verdadeiro):
 *     - Retorna [Resource.Success] contendo o corpo da resposta.
 * - Se a resposta não for bem-sucedida:
 *     - Retorna [Resource.Failure] com a mensagem de erro da resposta.
 * - Se ocorrer alguma exceção durante o processo:
 *     - Retorna [Resource.Failure] com uma mensagem de erro genérica incluindo a mensagem localizada da exceção.
 *
 * @param T O tipo do corpo da resposta.
 * @return Um objeto [Resource] representando o resultado da operação de rede.
 */
internal fun <T> Response<T>.asResource(): Resource<T> = mapToResource { it }

/**
 * Mensagem de erro padrão utilizada quando ocorre uma exceção inesperada durante
 * o mapeamento ou processamento da resposta de rede.
 */
private const val UNEXPECTED_ERROR_MESSAGE = "Erro inesperado"
