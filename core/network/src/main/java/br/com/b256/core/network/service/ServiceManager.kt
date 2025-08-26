package br.com.b256.core.network.service

import androidx.tracing.trace
import br.com.b256.core.common.monitor.NetworkMonitor
import br.com.b256.core.model.Pong
import br.com.b256.core.model.Resource
import br.com.b256.core.network.api.Api
import br.com.b256.core.network.mapper.asModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerencia os serviços de rede para a aplicação.
 *
 * Esta classe é responsável por criar e fornecer instâncias de serviços de API
 * usando Retrofit. Ela lida com chamadas de API e encapsula as respostas em um objeto [Resource]
 * para representar os estados de carregamento, sucesso e erro.
 *
 * @property retrofit A instância do Retrofit usada para criar os serviços de API.
 */
@Singleton
class ServiceManager @Inject constructor(
    private val network: NetworkMonitor,
    private val retrofit: Retrofit,
) : Service {
    private val service = retrofit.create(Api::class.java)

    /**
     * Realiza um ping no servidor e retorna um [Flow] de objetos [Resource].
     *
     * O [Flow] emitirá [Resource.Loading] quando a requisição for feita,
     * e então [Resource.Success] com o objeto [Pong] se a requisição for bem-sucedida,
     * ou [Resource.Error] se ocorrer um erro.
     *
     * Este método utiliza [safeApiFlow] para lidar com a lógica de rede comum,
     * como verificação de conectividade, emissão de estados de carregamento e tratamento de erros.
     *
     * @return Um [Flow] de objetos [Resource] que representam o estado da requisição.
     *   - [Resource.Loading]: Indica que a requisição está em andamento.
     *   - [Resource.Success]: Contém o objeto [Pong] se a requisição for bem-sucedida.
     *   - [Resource.Error]: Contém uma mensagem de erro se a requisição falhar.
     * @throws HttpException Se a resposta do servidor indicar um erro HTTP.
     * @throws IOException Se ocorrer um erro de I/O durante a comunicação com o servidor.
     */
    override suspend fun ping(): Flow<Resource<Pong>> = safeApiFlow(label = "Ping") {
        service.ping().let { response ->
            if (response.isSuccessful) {
                response.body()!!.asModel()
            } else {
                throw HttpException(response)
            }
        }
    }

    /**
     * Envolve uma chamada de API em um [Flow] de objetos [Resource].
     *
     * Este método lida com a verificação da disponibilidade da rede,
     * emitindo estados de carregamento, sucesso e erro, e lidando com exceções comuns
     * que podem ocorrer durante uma chamada de API.
     *
     * @param T O tipo do objeto de sucesso esperado da chamada de API.
     * @param label Um rótulo para fins de rastreamento e depuração.
     * @param call A função lambda suspensa que executa a chamada de API real.
     * @return Um [Flow] de objetos [Resource] que representam o estado da requisição.
     */
    private inline fun <T> safeApiFlow(
        label: String,
        crossinline call: suspend () -> T,
    ): Flow<Resource<T>> = flow {
        trace(label = "Network.$label") {
            // Verifique a disponibilidade da rede.
            if (network.isUnavailable.first()) {
                return@flow emit(value = Resource.Error("Sem conexão com a internet"))
            }

            // Emite um estado de carregamento.
            emit(value = Resource.Loading(true))

            try {
                // Executa a requisição.
                emit(Resource.Success(data = call()))
            } catch (e: Exception) {
                // Lida com exceções comuns
                emit(
                    value = Resource.Error(
                        message = when (e) {
                            is IOException -> e.localizedMessage.orEmpty()
                            is HttpException -> e.message.orEmpty()
                            else -> "Erro inesperado: ${e.localizedMessage.orEmpty()}"
                        },
                    ),
                )
            } finally {
                // Emite o fim do estado de carregamento.
                emit(value = Resource.Loading(false))
            }
        }
    }
}
