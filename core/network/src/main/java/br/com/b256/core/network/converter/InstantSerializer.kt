package br.com.b256.core.network.converter

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

/**
 * Um [kotlinx.serialization.KSerializer] para [kotlin.time.Instant] que serializa e desserializa objetos [kotlin.time.Instant]
 * para e a partir de sua representação de string ISO 8601.
 *
 * Este serializador é usado pelo kotlinx.serialization para converter objetos [kotlin.time.Instant]
 * quando eles fazem parte de um modelo de dados sendo serializado ou desserializado.
 *
 * Ele utiliza [kotlin.time.Instant.Companion.parse] para desserialização e [Instant.toJavaInstant().toString()]
 * para serialização.
 */
@OptIn(ExperimentalTime::class)
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    /**
     * Desserializa uma representação de string de um Instant a partir do decodificador.
     *
     * @param decoder O decodificador para ler a string.
     * @return O objeto Instant desserializado.
     */
    override fun deserialize(decoder: Decoder): Instant {
        return Instant.Companion.parse(decoder.decodeString())
    }

    /**
     * Serializa um [Instant] para sua representação de string ISO.
     *
     * @param encoder O codificador para escrever.
     * @param value O [Instant] a ser serializado.
     */
    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toJavaInstant().toString())
    }
}
