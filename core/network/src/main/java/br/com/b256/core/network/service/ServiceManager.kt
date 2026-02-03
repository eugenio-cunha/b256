package br.com.b256.core.network.service

import br.com.b256.core.model.Pong
import br.com.b256.core.model.Resource
import br.com.b256.core.network.api.ServerApi
import br.com.b256.core.network.mapper.asModel
import br.com.b256.core.network.mapper.asResourceFlow
import br.com.b256.core.network.util.NetworkTracer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ServiceManager @Inject constructor(
    private val api: ServerApi,
) : NetworkTracer(), Service {

    override suspend fun ping(): Flow<Resource<Pong>> = tracer(api) {
        ping()
    }.asResourceFlow { response ->
        response.asModel()
    }
}
