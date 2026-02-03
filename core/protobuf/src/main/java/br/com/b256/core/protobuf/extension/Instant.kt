package br.com.b256.core.protobuf.extension

import com.google.protobuf.Timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Converte um objeto Instant para um Timestamp do Protobuf.
 *
 * @return Um objeto Timestamp do Protobuf representando o mesmo ponto no tempo.
 */
@OptIn(ExperimentalTime::class)
fun Instant.asTimestamp(): Timestamp {
    return Timestamp.newBuilder()
        .setSeconds(this.epochSeconds)
        .setNanos(this.nanosecondsOfSecond)
        .build()
}
