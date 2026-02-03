package br.com.b256.core.network.di

import android.content.Context
import androidx.tracing.trace
import br.com.b256.core.datastore.CookieStore
import br.com.b256.core.network.BuildConfig
import br.com.b256.core.network.converter.InstantSerializer
import br.com.b256.core.network.service.Service
import br.com.b256.core.network.service.ServiceManager
import br.com.b256.core.network.util.CookieManager
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
    @OptIn(ExperimentalTime::class)
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

            // Serializa/Deserializa o tipo Instant/String ISO-8601
            serializersModule = SerializersModule {
                contextual(Instant::class, InstantSerializer)
            }
        }
        return json.asConverterFactory("application/json".toMediaType())
    }

    /**
     * Fornece uma instância singleton de [CookieManager].
     *
     * O [CookieManager] é responsável por gerenciar a persistência e a recuperação de cookies
     * utilizando um [CookieStore] e um [CoroutineScope] dedicado para operações assíncronas.
     *
     * @param store O armazenamento onde os cookies serão persistidos.
     * @param scope O escopo de corrotina utilizado para as operações do gerenciador de cookies.
     * @return Uma instância de [CookieManager] configurada.
     */
    @Provides
    @Singleton
    fun providesCookieManager(
        store: CookieStore,
        @CookieScope scope: CoroutineScope,
    ): CookieManager =
        CookieManager(store = store, scope = scope)


    /**
     * Fornece a instância de [Call.Factory] do OkHttp configurada para o aplicativo.
     *
     * Esta configuração inclui:
     * - Gerenciamento de cookies através do [CookieManager].
     * - Logging de rede configurado para o nível [HttpLoggingInterceptor.Level.BODY] apenas em builds de depuração (debug).
     *
     * @param cookieManager O gerenciador responsável por persistir e recuperar cookies de rede.
     * @return Uma instância de [Call.Factory] (OkHttpClient) configurada.
     */
    @Provides
    @Singleton
    fun providesOkHttpCallFactory(cookieManager: CookieManager): Call.Factory =
        OkHttpClient.Builder()
            .cookieJar(cookieManager)
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    },
            )
            .build()


    /**
     * Fornece uma instância configurada do [Retrofit] para realizar chamadas de rede.
     *
     * O cliente é configurado com a URL base definida no [BuildConfig.BASE_URL], a factory de
     * conversão de dados e a factory de chamadas HTTP.
     *
     * @param converterFactory A factory responsável pela serialização e desserialização dos dados (JSON).
     * @param okHttpCallFactory A factory de chamadas HTTP (OkHttpClient) para processar as requisições.
     * @return Uma instância de [Retrofit] configurada.
     * @throws IllegalArgumentException Se a URL base for nula.
     */
    @Provides
    @Singleton
    internal fun providesRetrofit(
        converterFactory: Converter.Factory,
        okHttpCallFactory: Call.Factory,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(requireNotNull(BuildConfig.BASE_URL) { "A URL base da API não pode ser nula!" })
        .addConverterFactory(converterFactory)
        .callFactory(okHttpCallFactory)
        .build()

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
internal abstract class BindModule {

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

/**
 * Qualifier para identificar o [CoroutineScope] específico de cookies.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class CookieScope
