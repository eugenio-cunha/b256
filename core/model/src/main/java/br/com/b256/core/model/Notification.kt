package br.com.b256.core.model

/**
 * Representa um objeto de notificação.
 *
 * @property id O identificador único da notificação.
 * @property title O título da notificação.
 * @property content O conteúdo ou corpo da notificação.
 * @property deepLink Uma URL ou caminho que direciona o usuário para um local específico dentro do aplicativo quando a notificação é tocada.
 */
data class Notification(
    val id: String,
    val title: String,
    val content: String,
    val deepLink: String,
)
