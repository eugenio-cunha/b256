package br.com.b256.core.common.monitor

import kotlinx.coroutines.flow.Flow

/**
 * Utilitário para informar o status de conectividade do aplicativo
 */
interface NetworkMonitor {
    /**
     * Emite `true` se a rede estiver disponível, `false` caso contrário.
     */
    val isAvailable: Flow<Boolean>

    /**
     * Emite `true` quando a rede está disponível e `false` caso contrário.
     */
    val isUnavailable: Flow<Boolean>
}
