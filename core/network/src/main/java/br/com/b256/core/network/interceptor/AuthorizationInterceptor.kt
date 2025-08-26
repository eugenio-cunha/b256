package br.com.b256.core.network.interceptor

import br.com.b256.core.datastore.Preference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection
import javax.inject.Inject

/**
 * [AuthorizationInterceptor] é um interceptador do OkHttp que adiciona cabeçalhos de autenticação e User-Agent às requisições e lida com respostas não autorizadas.
 *
 * Este interceptador é responsável por:
 * 1. Adicionar o token de autenticação ao cabeçalho "Authorization" de cada requisição.
 * 2. Adicionar o cabeçalho "User-Agent" com um valor fixo para contornar possíveis restrições de firewall.
 * 3. Verificar se a resposta tem o código HTTP 401 (Não Autorizado). Se a resposta for 401 e a requisição não for para a rota de login, realizar o logout do usuário.
 *
 * @property preference Armazena e recupera dados de autenticação, como o token e o estado de autenticação.
 */
internal class AuthorizationInterceptor @Inject constructor(
    private val preference: Preference,
) : Interceptor {

    /**
     * Recupera o token de autenticação do [Preference] de forma síncrona.
     *
     * Este método é executado em um contexto de corrotina de IO para garantir que a operação de leitura do [preference] não bloqueie a thread principal.
     *
     * @return O token de autenticação.
     */
    private fun getToken(): String {
        return runBlocking(Dispatchers.IO) {
            preference.getSession().firstOrNull()?.token.orEmpty()
        }
    }

    /**
     * Limpa os dados de autenticação do [Preference], garantindo execução síncrona.
     *
     * Este método limpa os dados de autenticação do [preference] se o usuário estiver autorizado.
     * A operação é executada em uma corrotina de IO para evitar bloqueios na thread principal.
     */
    private fun unauthorized() {
        CoroutineScope(Dispatchers.IO).launch {
            preference.clean()
        }
    }

    /**
     * Intercepta a requisição e adiciona os cabeçalhos de autenticação e User-Agent.
     *
     * Este método também verifica se a resposta tem o código HTTP 401 (Não Autorizado) e, se for o caso,
     * realiza o logout do usuário, desde que a requisição não seja para a rota de autenticação.
     *
     * @param chain A cadeia de interceptadores.
     * @return A resposta da requisição.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = request.newBuilder().let {
            // O cabeçalho de requisição HTTP Authorization contém as credenciais para autenticar o agente de usuário com o servidor
            it.header(KEY_AUTHORIZATION, "Bearer ${getToken()}")

            // Escapa das regras contra os BOTS no AWS WAF (Web Application Firewall).
            .header(KEY_AGENT, KEY_AGENT_VALUE)

            chain.proceed(it.build())
        }

        // Em caso de código 401 na resposta, força o logout do usuário.
        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED &&
            !request.url.encodedPath.contains("login")
        ) {
            unauthorized()
        }

        return response
    }

    companion object {
        /**
         * Chave para o cabeçalho User-Agent.
         */
        const val KEY_AGENT = "User-Agent"

        /**
         * Valor para o cabeçalho User-Agent.
         */
        const val KEY_AGENT_VALUE =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.6 Safari/605.1.15"

        /**
         * Chave para o cabeçalho de autorização.
         */
        const val KEY_AUTHORIZATION = "Authorization"
    }
}
