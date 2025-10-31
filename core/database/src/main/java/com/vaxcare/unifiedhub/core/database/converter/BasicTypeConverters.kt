package com.vaxcare.unifiedhub.core.database.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * Classes used for serializing and deserializing data from the SqLite database.
 *
 * BasicTypeConverters holds all the converters for basic types that are included in the core
 * libraries and are not custom classes written by us at VaxCare.
 *
 * The basic syntax for adding a converter is:
 *
 * ```
 * @TypeConverter
 * open fun toT(value: S): T {
 *     return T
 * }
 *
 * @TypeConverter
 * open fun fromT(value: T): S {
 *     return S
 * }
 * ```
 *
 * Where T and S are the classes to serialize to and from. Note: SqLite only supports primitive
 * types, so S should be a primitive and T should be the class you desire.
 */
class BasicTypeConverters {
    @TypeConverter
    fun toInstant(long: Long?) =
        if (long != null) {
            Instant.ofEpochMilli(long)
        } else {
            null
        }

    @TypeConverter
    fun fromInstant(instant: Instant?) = instant?.toEpochMilli()

    @TypeConverter
    fun toLocalDate(long: Long?) =
        if (long != null) {
            Instant.ofEpochMilli(long).atZone(ZoneId.systemDefault()).toLocalDate()
        } else {
            null
        }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?) = date?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun toLocalDateTime(long: Long?) =
        if (long != null) {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(long), ZoneId.systemDefault())
        } else {
            null
        }

    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime?) = date?.toInstant(ZoneOffset.UTC)?.toEpochMilli()

    @TypeConverter
    fun fromIntListToString(satisfyingProductIds: List<Int>): String {
        val iterator = satisfyingProductIds.iterator()
        var productListString = ""
        iterator.forEach {
            productListString += it.toString()
            if (iterator.hasNext()) {
                productListString += ','
            }
        }
        return productListString
    }

    @TypeConverter
    fun toIntListFromString(satisfyingProductIds: String): List<Int> {
        val iterator = satisfyingProductIds.split(',').iterator()
        val productListInt = mutableListOf<Int>()
        iterator.forEach { productListInt.add(it.toInt()) }
        return productListInt
    }
}
