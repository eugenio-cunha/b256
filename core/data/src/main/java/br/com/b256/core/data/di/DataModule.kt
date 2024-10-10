package br.com.b256.core.data.di

import br.com.b256.core.data.repository.NetworkRepository
import br.com.b256.core.data.repository.NetworkRepositoryImpl
import br.com.b256.core.network.service.Service
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    internal fun providesNetworkRepository(service: Service): NetworkRepository =
        NetworkRepositoryImpl(service = service)
}
