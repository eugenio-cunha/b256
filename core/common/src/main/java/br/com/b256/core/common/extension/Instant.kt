package br.com.b256.core.common.extension

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

/**
 * Converte um [Instant] para uma string de data e hora no formato brasileiro (dd/MM/yyyy - HH:mm:ss).
 *
 * @param timeZone O [TimeZone] a ser usado para a conversão. O padrão é [TimeZone.currentSystemDefault].
 * @return A string de data e hora no formato brasileiro.
 */
@OptIn(ExperimentalTime::class)
fun Instant.toBrazilianDateTimeString(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    with(toLocalDateTime(timeZone)) {
        val day = day.toString().padStart(2, '0')
        val month = month.number.toString().padStart(2, '0')

        val h = hour.toString().padStart(2, '0')
        val m = minute.toString().padStart(2, '0')
        val s = second.toString().padStart(2, '0')

        return "$day/$month/$year - $h:$m:$s"
    }
}

/**
 * Converte um [Instant] para uma string de data no formato brasileiro (dd/MM/yyyy).
 *
 * @param timeZone O [TimeZone] a ser usado para a conversão. O padrão é o fuso horário atual do sistema.
 * @return Uma string representando a data no formato brasileiro (dd/MM/yyyy).
 */
@OptIn(ExperimentalTime::class)
fun Instant.toBrazilianDateString(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    with(toLocalDateTime(timeZone)) {
        val day = day.toString().padStart(2, '0')
        val month = month.number.toString().padStart(2, '0')

        return "$day/$month/$year"
    }
}

/**
 * Converte este [Instant] para sua representação de string no formato ISO 8601.
 *
 * Esta função utiliza o método `toString()` da classe Java `Instant`,
 * que produz uma string no formato ISO 8601 (por exemplo, "2007-12-03T10:15:30.00Z").
 *
 * @return Uma string representando este instante no formato ISO 8601.
 */
@OptIn(ExperimentalTime::class)
fun Instant.toIsoString(): String = toJavaInstant().toString()

/**
 * Verifica se dois objetos `Instant` representam a mesma data.
 *
 * Esta função compara apenas a parte da data (dia, mês e ano) de dois objetos `Instant`,
 * ignorando a hora, minuto, segundo e nanossegundo. A comparação é feita
 * convertendo ambos os `Instant` para `LocalDateTime` usando o `timeZone` fornecido
 * e, em seguida, comparando suas propriedades `date`.
 *
 * @param value O outro objeto `Instant` para comparar.
 * @param timeZone O fuso horário a ser usado para a conversão de ambos os `Instant`.
 *                 O padrão é o fuso horário atual do sistema (`TimeZone.currentSystemDefault()`).
 * @return `true` se ambos os `Instant` representarem a mesma data (dia, mês e ano)
 *         no `timeZone` especificado, `false` caso contrário.
 */
@OptIn(ExperimentalTime::class)
internal fun Instant.isSame(value: Instant, timeZone: TimeZone = TimeZone.UTC): Boolean {
    val date1 = toLocalDateTime(timeZone).date
    val date2 = value.toLocalDateTime(timeZone).date
    return date1 == date2
}

/**
 * Verifica se a data deste `Instant` é anterior à data de outro `Instant`.
 *
 * Esta função compara apenas as partes da data (dia, mês e ano) dos dois `Instant`s,
 * ignorando as horas, minutos e segundos.
 *
 * @param value O `Instant` com o qual comparar.
 * @param timeZone O fuso horário a ser usado para a conversão de ambos os `Instant`s para datas locais.
 *                 O padrão é o fuso horário atual do sistema.
 * @return `true` se a data deste `Instant` for anterior à data do `instant` fornecido,
 *         `false` caso contrário.
 */
@OptIn(ExperimentalTime::class)
internal fun Instant.isBefore(value: Instant, timeZone: TimeZone = TimeZone.UTC): Boolean {
    val date1 = toLocalDateTime(timeZone).date
    val date2 = value.toLocalDateTime(timeZone).date
    return date1 < date2
}

/**
 * Verifica se a data do `Instant` atual é posterior à data de outro `Instant`.
 *
 * Esta função compara apenas a parte da data (dia, mês e ano) dos dois `Instant`s,
 * ignorando a hora, minuto, segundo e nanossegundo.
 *
 * @param value O `Instant` com o qual comparar.
 * @param timeZone O fuso horário a ser usado para a conversão. O padrão é o fuso horário do sistema.
 * @return `true` se a data do `Instant` atual for posterior à data do `instant` fornecido,
 *         `false` caso contrário.
 */
@OptIn(ExperimentalTime::class)
internal fun Instant.isAfter(value: Instant, timeZone: TimeZone = TimeZone.UTC): Boolean {
    val date1 = toLocalDateTime(timeZone).date
    val date2 = value.toLocalDateTime(timeZone).date
    return date1 > date2
}
