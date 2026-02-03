package br.com.b256.core.datastore.serializer

import androidx.datastore.core.Serializer
import br.com.b256.core.datastore.StringListMapProto
import br.com.b256.core.datastore.serializer.StringListMapSerializer.defaultValue
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

/**
 * Serializer para [StringListMapProto] utilizado com DataStore.
 *
 * Este serializer converte dados entre o formato Protocol Buffer ([StringListMapProto])
 * e streams de bytes para armazenamento persistente no DataStore.
 *
 * ## Características
 * - **Thread-safe**: Seguro para uso concorrente
 * - **Recuperação de erros**: Retorna valor padrão em caso de dados corrompidos
 * - **Imutável**: Implementado como object singleton
 *
 * ## Uso
 * ```kotlin
 * val dataStore: DataStore<StringListMapProto> = DataStoreFactory.create(
 *     serializer = StringListMapSerializer,
 *     produceFile = { context.dataStoreFile("string_list_map.pb") }
 * )
 * ```
 *
 * @see StringListMapProto
 * @see Serializer
 */
object StringListMapSerializer : Serializer<StringListMapProto> {

    /**
     * Valor padrão retornado quando:
     * - O arquivo de dados ainda não existe
     * - Ocorre um erro de parsing (dados corrompidos)
     * - A leitura falha por qualquer motivo
     *
     * Retorna uma instância vazia de [StringListMapProto] sem nenhum dado.
     */
    override val defaultValue: StringListMapProto = StringListMapProto.getDefaultInstance()

    /**
     * Desserializa dados do [InputStream] para um objeto [StringListMapProto].
     *
     * Este método é chamado pelo DataStore quando precisa ler dados do armazenamento persistente.
     *
     * ## Tratamento de erros
     * Se o parsing falhar devido a dados corrompidos ou formato inválido,
     * o método retorna [defaultValue] em vez de propagar a exceção, garantindo
     * que o DataStore possa se recuperar graciosamente de estados corrompidos.
     *
     * @param input O [InputStream] contendo os dados serializados em Protocol Buffer.
     * @return Uma instância de [StringListMapProto] com os dados desserializados,
     *         ou [defaultValue] se o parsing falhar.
     *
     * @see InvalidProtocolBufferException
     */
    override suspend fun readFrom(input: InputStream): StringListMapProto =
        try {
            StringListMapProto.parseFrom(input)
        } catch (_: InvalidProtocolBufferException) {
            defaultValue
        }

    /**
     * Serializa um objeto [StringListMapProto] para o [OutputStream].
     *
     * Este método é chamado pelo DataStore quando precisa persistir dados no armazenamento.
     * Os dados são escritos no formato Protocol Buffer binário.
     *
     * ## Garantias
     * - A operação é atômica do ponto de vista do DataStore
     * - Os dados são escritos de forma eficiente no formato binário compacto
     * - Não há buffering adicional necessário
     *
     * @param t A instância de [StringListMapProto] a ser serializada.
     * @param output O [OutputStream] onde os dados serializados serão escritos.
     *
     * @throws java.io.IOException se ocorrer um erro de I/O durante a escrita.
     */
    override suspend fun writeTo(
        t: StringListMapProto,
        output: OutputStream,
    ) = t.writeTo(output)
}
