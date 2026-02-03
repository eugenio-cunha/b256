package br.com.b256.core.protobuf.extension

import com.google.protobuf.Timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Converte um [Timestamp] para um [Instant].
 *
 * @return O [Instant] equivalente a este [Timestamp].
 */
@OptIn(ExperimentalTime::class)
fun Timestamp.asInstant(): Instant =
    Instant.fromEpochSeconds(this.seconds, this.nanos.toLong())
