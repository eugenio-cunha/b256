package br.com.b256.core.datastore

/**
 * Interface para armazenar e recuperar cookies.
 */
interface CookieStore {
    /**
     * Recupera todos os cookies armazenados.
     *
     * @return Um mapa onde a chave é o identificador e o valor é a lista de cookies associada.
     */
    suspend fun all(): Map<String, List<String>>

    /**
     * Salva uma lista de cookies associada a uma chave específica.
     *
     * @param key A chave identificadora para o conjunto de cookies (ex: domínio ou nome do cookie).
     * @param value A lista de strings representando os valores dos cookies a serem armazenados.
     */
    suspend fun save(key: String, value: List<String>)

    /**
     * Limpa todos os cookies do data store.
     */
    suspend fun clear()
}
