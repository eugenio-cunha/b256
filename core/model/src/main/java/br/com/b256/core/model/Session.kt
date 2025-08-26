package br.com.b256.core.model

/**
 * Representa uma sessão de usuário.
 *
 * @property token O identificador único para a sessão.
 */
data class Session(
    val token: String
)
