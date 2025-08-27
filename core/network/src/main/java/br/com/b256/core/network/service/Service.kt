package br.com.b256.core.network.service

import br.com.b256.core.model.Resource
import br.com.b256.core.model.Pong
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

/**
 * Gerencia os serviços de rede para a aplicação.
 *
 * Esta classe é responsável por criar e fornecer instâncias de serviços de API
 * usando Retrofit. Ela lida com chamadas de API e encapsula as respostas em um objeto [Resource]
 * para representar os estados de carregamento, sucesso e erro.
 *
 * @property retrofit A instância do Retrofit usada para criar os serviços de API.
 */
interface Service {
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
    suspend fun ping(): Flow<Resource<Pong>>
}
