package br.com.b256.core.network.di

import android.content.Context
import androidx.tracing.trace
import br.com.b256.core.datastore.Preference
import br.com.b256.core.network.BuildConfig
import br.com.b256.core.network.interceptor.AuthorizationInterceptor
import br.com.b256.core.network.interceptor.ExceptionInterceptor
import br.com.b256.core.network.service.Service
import br.com.b256.core.network.service.ServiceManager
import coil3.ImageLoader
import coil3.svg.SvgDecoder
import coil3.util.DebugLogger
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Módulo Dagger Hilt que fornece dependências de rede para o aplicativo.
 * Este módulo é instalado no [SingletonComponent], o que significa que as dependências fornecidas
 * terão um escopo singleton e viverão durante todo o ciclo de vida do aplicativo.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object ProvideModule {

    /**
     * Fornece uma [Converter.Factory] para serialização e desserialização JSON.
     *
     * Esta factory é configurada com regras específicas de processamento JSON:
     * - `ignoreUnknownKeys = true`: Ignora campos extras da API que não estão definidos nos modelos de dados.
     * - `explicitNulls = false`: Campos nulos no objeto não são incluídos na saída JSON.
     * - `isLenient = true`: Permite uma análise mais tolerante de APIs que não aderem estritamente ao padrão JSON.
     *
     * @return Uma instância de [Converter.Factory] configurada para processamento JSON.
     */
    @Provides
    @Singleton
    fun providesNetworkJson(): Converter.Factory {
        val json = Json {
            // Ignora campos extras da API
            ignoreUnknownKeys = true

            // Todos os campos nulos do objeto não são incluídos no JSON
            explicitNulls = false

            // APIs que não seguem estritamente o padrão JSON o parser se torna mais tolerante
            isLenient = true
        }
        return json.asConverterFactory("application/json".toMediaType())
    }

    /**
     * Fornece uma instância singleton de [Call.Factory] para chamadas de rede.
     *
     * Esta factory é configurada com um interceptor de log HTTP que registra o corpo
     * das requisições e respostas em builds de depuração.
     *
     * @return Uma instância de [Call.Factory] configurada.
     */
    @Provides
    @Singleton
    fun providesOkHttpCallFactory(): Call.Factory = trace("B256OkHttpClient") {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    },
            )
            .build()
    }

    /**
     * Fornece uma instância do Retrofit para realizar chamadas de API.
     *
     * Esta função configura o Retrofit com um OkHttpClient personalizado que inclui:
     * - Tempos limite de conexão, leitura e escrita de 180 segundos.
     * - Um [ExceptionInterceptor] para lidar com exceções de rede.
     * - Um [AuthorizationInterceptor] para adicionar cabeçalhos de autorização às requisições, usando a [preference] fornecida para recuperar tokens.
     *
     * A instância do Retrofit é configurada com:
     * - A URL base de [BuildConfig.BASE_URL].
     * - O [converterFactory] fornecido para serializar e desserializar respostas da API.
     *
     * @param preference A instância de [Preference] usada para recuperar tokens de autorização.
     * @param converterFactory O [Converter.Factory] usado para serialização e desserialização JSON.
     * @return Uma instância configurada do [Retrofit].
     * @throws IllegalArgumentException se [BuildConfig.BASE_URL] for nulo.
     */
    @Provides
    @Singleton
    internal fun providesRetrofit(
        preference: Preference,
        converterFactory: Converter.Factory,
    ): Retrofit {
        val client = OkHttpClient.Builder()
            .apply {
                connectTimeout(180, TimeUnit.SECONDS)
                readTimeout(180, TimeUnit.SECONDS)
                writeTimeout(180, TimeUnit.SECONDS)
                addInterceptor(ExceptionInterceptor())
                addInterceptor(AuthorizationInterceptor(preference = preference))
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(requireNotNull(BuildConfig.BASE_URL) { "A URL base da API não pode ser nula!" })
            .addConverterFactory(converterFactory)
            .client(client)
            .build()
    }

    /**
     * Como estamos exibindo SVGs no aplicativo, o Coil precisa de um ImageLoader que suporte este
     * formato. Durante a inicialização do Coil, ele chamará `applicationContext.newImageLoader()`
     * para obter um ImageLoader.
     *
     * @param okHttpCallFactory Fábrica de chamadas OkHttp para carregar imagens. Solicitamos
     * especificamente `dagger.Lazy` aqui, para que ele não seja instanciado a partir do Dagger.
     * @param context Contexto da aplicação.
     * @return Um ImageLoader que suporta o formato SVG.
     * @see <a href="https://github.com/coil-kt/coil/blob/main/coil-singleton/src/main/java/coil/Coil.kt">Coil</a>
     */
    @Provides
    @Singleton
    fun providesImageLoader(
        // Solicitamos especificamente dagger.Lazy aqui, para que ele não seja instanciado a partir do Dagger.
        okHttpCallFactory: dagger.Lazy<Call.Factory>,
        @ApplicationContext context: Context,
    ): ImageLoader = trace("B256ImageLoader") {
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .logger(if (BuildConfig.DEBUG) DebugLogger() else null)

            .build()
    }
}

/**
 * Módulo Dagger Hilt para fornecer dependências de serviço.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {

    /**
     * Fornece uma instância de [Service] vinculando a implementação [ServiceManager].
     *
     * @param impl A implementação de [Service].
     * @return Uma instância de [Service].
     */
    @Binds
    @Reusable
    abstract fun bindService(impl: ServiceManager): Service
}
