package com.vaxcare.unifiedhub.core.common.ext

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Converts an [String] to a [LocalDate]
 *
 * @param formatter the [DateTimeFormatter] instance you want to apply to the given String
 *
 * @return Returns the [LocalDate] instance or null if the string format was not valid for the
 * format provided
 */
fun String.toLocalDate(formatter: DateTimeFormatter): LocalDate? =
    try {
        LocalDate.parse(this, formatter)
    } catch (e: DateTimeParseException) {
        null
    }

/**
 * Converts an [String] to a [LocalDateTime]
 *
 * @param formatter the [DateTimeFormatter] instance you want to apply to the given String
 *
 * @return Returns the [LocalDateTime] instance or null if the string format was not valid for the
 * format provided
 */
fun String.toLocalDateTime(formatter: DateTimeFormatter): LocalDateTime? =
    try {
        LocalDateTime.parse(this, formatter)
    } catch (e: DateTimeParseException) {
        null
    }

/**
 * Converts an [String] to a [Instant]
 *
 * @param formatter the [DateTimeFormatter] instance you want to apply to the given String
 *
 * @return Returns the [Instant] instance or null if the string format was not valid for the
 * format provided
 */
fun String.toInstant(formatter: DateTimeFormatter): Instant? =
    this.toLocalDateTime(formatter)?.toInstant(ZoneOffset.UTC)
