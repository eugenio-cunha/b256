package br.com.b256.core.model

/**
 * Representa a resposta de uma solicitação de pong.
 *
 * @property result O resultado da solicitação de pong.
 * @property success Indica se a solicitação de pong foi bem-sucedida.
 */
data class Pong(
    val result: String,
    val success: String,
)
