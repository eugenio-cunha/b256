package br.com.b256.core.database.converter

import androidx.room.TypeConverter
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
class InstantConverter {
    @TypeConverter
    fun fromInstant(value: Instant?): Long? {
        if (value == null) {
            return null
        }

        return value.toEpochMilliseconds()

    }

    @TypeConverter
    fun toInstant(value: Long?): Instant? {
        if (value == null) {
            return null
        }

        return Instant.fromEpochMilliseconds(value)
    }
}
