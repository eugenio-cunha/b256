package br.com.b256.core.common.monitor

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.NetworkRequest.Builder
import androidx.core.content.getSystemService
import androidx.tracing.trace
import br.com.b256.core.common.B256Dispatchers.IO
import br.com.b256.core.common.Dispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ManagerNetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : NetworkMonitor {

    override val isAvailable: Flow<Boolean> = callbackFlow {
        trace("NetworkMonitor.callbackFlow") {
            val connectivityManager = context.getSystemService<ConnectivityManager>()
            if (connectivityManager == null) {
                channel.trySend(false)
                channel.close()
                return@callbackFlow
            }

            /**
             * Os métodos do retorno de chamada são invocados em alterações em *qualquer* rede que corresponda à [NetworkRequest],
             * não apenas na rede ativa. Assim, podemos simplesmente rastrear a presença (ou ausência) dessa [Network].
             */
            val callback = object : NetworkCallback() {

                private val networks = mutableSetOf<Network>()

                override fun onAvailable(network: Network) {
                    networks += network
                    channel.trySend(true)
                }

                override fun onLost(network: Network) {
                    networks -= network
                    channel.trySend(networks.isNotEmpty())
                }
            }

            trace("NetworkMonitor.registerNetworkCallback") {
                val request = Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
                connectivityManager.registerNetworkCallback(request, callback)
            }

            /**
             * Envia o status de conectividade mais recente para o canal subjacente.
             */
            channel.trySend(connectivityManager.isCurrentlyConnected())

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }
    }
        .flowOn(ioDispatcher)
        .conflate()

    override val isUnavailable: Flow<Boolean> = isAvailable.map { connected -> !connected }

    /**
     * Retorna true se a rede ativa tiver capacidade de internet.
     */
    private fun ConnectivityManager.isCurrentlyConnected() = activeNetwork
        ?.let(::getNetworkCapabilities)
        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
}
