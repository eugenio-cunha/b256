package br.com.b256.core.network.util

import android.util.Log
import androidx.tracing.trace
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import retrofit2.Response
import kotlin.coroutines.cancellation.CancellationException


/**
 * Classe base abstrata projetada para fornecer uma execução segura e rastreável de chamadas de rede.
 *
 * O [NetworkTracer] centraliza a lógica de tratamento de exceções, monitoramento de performance
 * via [androidx.tracing.trace] e padronização de respostas de erro para as camadas de dados.
 * Ele garante que falhas de rede não causem interrupções inesperadas no fluxo do aplicativo,
 * convertendo exceções em instâncias de [Response.error].
 */
internal abstract class NetworkTracer {
    /**
     * Executa uma chamada de rede e a rastreia utilizando `androidx.tracing.trace`.
     *
     * Esta função atua como um invólucro (wrapper) para chamadas de API, fornecendo rastreamento de
     * sistema, captura de exceções e tratamento padronizado de erros. O rótulo do rastreamento
     * é gerado dinamicamente com base no nome da classe da API fornecida.
     *
     * @param A O tipo da interface da API ou serviço.
     * @param R O tipo de dado esperado no corpo da resposta do Retrofit.
     * @param api A instância da API na qual a chamada será executada.
     * @param block Uma função lambda de suspensão, executada no contexto de [api], que
     *              deve retornar um [Response] do Retrofit.
     * @return O [Response] resultante da execução do [block]. Em caso de falha (IOException
     *         ou outras exceções), retorna um [Response.error] com código 500.
     * @throws CancellationException Relança exceções de cancelamento para garantir o comportamento
     *         correto das Coroutines.
     */
    protected suspend inline fun <A, R> tracer(
        api: A,
        crossinline block: suspend A.() -> Response<R>,
    ): Response<R> =
        try {
            trace(label = "Network.${api!!::class.simpleName}") {
                api.block()
            }
        } catch (ce: CancellationException) {
            log(ce)
            throw ce // IMPORTANTE: sempre relançar, senão quebra o comportamento das coroutines
        } catch (io: IOException) {
            log(io)
            failure(io.message)
        } catch (throwable: Throwable) {
            log(throwable)
            failure(throwable.message)
        }

    /**
     * Registra mensagens de erro no Logcat para fins de depuração.
     *
     * @param throwable A exceção capturada durante a execução da rede.
     */
    private fun log(throwable: Throwable) {
        Log.d("NetworkTracer", throwable.message.orEmpty())
    }

    /**
     * Cria uma resposta de erro padronizada (HTTP 500) para falhas internas.
     *
     * @param R O tipo esperado da resposta.
     * @param message A mensagem de erro opcional. Se nula, utiliza um texto padrão.
     * @return Um [Response] do Retrofit configurado com o corpo do erro em texto plano.
     */
    private fun <R> failure(message: String?): Response<R> =
        Response.error(
            500,
            (message ?: "Erro desconhecido")
                .toResponseBody("text/plain".toMediaType()),
        )
}
