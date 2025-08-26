package br.com.b256.core.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject

/**
 * Interceptor para lidar com exceções que ocorrem durante requisições de rede.
 *
 * Este interceptor verifica se a resposta foi bem-sucedida. Caso contrário, ele tenta analisar a
 * mensagem de erro do corpo da resposta JSON e reconstrói a resposta com a mensagem de erro extraída
 * e o código de status e corpo originais. Se alguma exceção ocorrer durante este processo (por exemplo,
 * ao analisar o JSON ou ao reconstruir a resposta), ele registra o erro utilizando `Log.e`
 * e retorna a resposta original para garantir que o fluxo da aplicação não seja interrompido abruptamente.
 */
internal class ExceptionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Caso a resposta foi bem-sucedida
        if (response.isSuccessful) return response

        return try {
            val code = response.code
            val body = response.body

            val message = runCatching {
                JSONObject(response.peekBody(Long.MAX_VALUE).string()).optString(
                    "message",
                    "Erro desconhecido",
                )
            }.getOrDefault("Erro desconhecido")

            response.newBuilder()
                .message(message = message)
                .code(code = code)
                .body(body = body)
                .build()
        } catch (e: Exception) {
            Log.e("ExceptionInterceptor", "Erro no Interceptor", e)
            response
        }
    }
}
