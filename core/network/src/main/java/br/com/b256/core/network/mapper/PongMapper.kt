package br.com.b256.core.network.mapper

import br.com.b256.core.model.Pong
import br.com.b256.core.network.model.PongResponse

/**
 * Converte um modelo de rede [PongResponse] para um modelo de domínio [Pong].
 */
fun PongResponse.asModel(): Pong = Pong(
    result = result,
    success = success,
)
