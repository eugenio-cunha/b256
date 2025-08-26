package br.com.b256.core.model

import br.com.b256.core.model.enums.Theme

/**
 * Representa as configurações do aplicativo do usuário.
 *
 * @property biometrics Indica se a autenticação biométrica está ativada.
 * @property theme O tema selecionado do aplicativo.
 */
data class Settings(
    val biometrics: Boolean,
    val theme: Theme,
)
