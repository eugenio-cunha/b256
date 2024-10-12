package br.com.core.b256.domain

import br.com.b256.core.common.Resource
import br.com.b256.core.data.repository.NetworkRepository
import br.com.b256.core.model.Pong
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetPingUseCase @Inject constructor(
    private val repository: NetworkRepository,
) {
    suspend operator fun invoke(): Flow<Resource<Pong>> {
        return repository.ping()
    }
}
