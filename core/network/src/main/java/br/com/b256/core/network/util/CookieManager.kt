package br.com.b256.core.network.util

import br.com.b256.core.datastore.CookieStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * Uma implementação de [CookieJar] que armazena cookies em um [CookieStore].
 *
 * Esta classe é responsável por salvar cookies recebidos de respostas HTTP e carregá-los
 * para requisições HTTP subsequentes. Ela utiliza um [CookieStore] para persistir os cookies
 * e um cache em memória para acesso rápido.
 *
 * A sincronização entre o cache e o armazenamento persistente é gerenciada automaticamente:
 * - Na inicialização, todos os cookies salvos são carregados do [CookieStore] para o cache
 * - Novos cookies são salvos simultaneamente no cache e no [CookieStore]
 *
 * ## Thread-Safety
 * Esta implementação é thread-safe, utilizando [ConcurrentHashMap] para o cache interno
 * e [CoroutineScope] para operações assíncronas de I/O.
 *
 * @property store O [CookieStore] utilizado para armazenar e recuperar cookies de forma persistente.
 * @property scope O [CoroutineScope] utilizado para executar operações assíncronas de carregamento e salvamento.
 *
 * @constructor Cria uma nova instância de [CookieManager].
 */
internal class CookieManager @Inject constructor(
    private val store: CookieStore,
    private val scope: CoroutineScope,
) : CookieJar {

    /**
     * Cache em memória de cookies indexados por host.
     *
     * Utiliza [ConcurrentHashMap] para garantir thread-safety em operações concorrentes.
     * A chave é o hostname e o valor é uma lista mutável de cookies válidos para esse host.
     */
    private val cache = ConcurrentHashMap<String, MutableList<Cookie>>()

    /**
     * Inicializa o cache carregando todos os cookies persistidos no [CookieStore].
     *
     * Este processo é executado de forma assíncrona no [scope] fornecido:
     * 1. Recupera todos os cookies armazenados agrupados por host
     * 2. Converte cada host em [HttpUrl] para validação
     * 3. Parseia as strings de cookies em objetos [Cookie]
     * 4. Filtra hosts que não possuem cookies válidos
     * 5. Popula o cache com os cookies válidos
     *
     * Cookies inválidos ou que não podem ser parseados são descartados silenciosamente.
     */
    init {
        scope.launch {
            store.all()
                .mapNotNull { (host, cookies) ->
                    host.toHttpUrlOrNull()?.let { url ->
                        host to cookies.mapNotNull { Cookie.parse(url, it) }
                    }
                }
                .filter { (_, cookies) -> cookies.isNotEmpty() }
                .forEach { (host, cookies) ->
                    cache[host] = cookies.toMutableList()
                }
        }
    }

    /**
     * Salva cookies recebidos de uma resposta HTTP.
     *
     * Os cookies são salvos em duas camadas:
     * 1. **Cache em memória**: Atualização síncrona para acesso imediato
     * 2. **Armazenamento persistente**: Salvamento assíncrono via [CookieStore] em [Dispatchers.IO]
     *
     * Cookies existentes para o mesmo host são substituídos completamente pela nova lista.
     *
     * @param url A URL da requisição HTTP que originou a resposta. O host desta URL é usado como chave de armazenamento.
     * @param cookies A lista de cookies a serem salvos, conforme recebidos na resposta HTTP.
     */
    override fun saveFromResponse(
        url: HttpUrl,
        cookies: List<Cookie>,
    ) {
        cache[url.host] = cookies.toMutableList()

        scope.launch(Dispatchers.IO) {
            store.save(url.host, cookies.map { it.toString() })
        }
    }

    /**
     * Carrega cookies para uma requisição HTTP.
     *
     * Retorna todos os cookies armazenados para o host da URL fornecida.
     * A busca é feita apenas no cache em memória para garantir desempenho.
     *
     * ## Comportamento
     * - Se existirem cookies para o host, retorna uma cópia imutável da lista
     * - Se não existirem cookies para o host, retorna uma lista vazia
     * - A lista retornada é uma cópia, portanto modificações não afetam o cache
     *
     * @param url A URL da requisição HTTP para a qual os cookies devem ser carregados.
     * @return Uma lista imutável de [Cookie] associados ao host da URL, ou uma lista vazia se não houver cookies.
     */
    override fun loadForRequest(url: HttpUrl): List<Cookie> = cache[url.host]?.toList().orEmpty()
}
