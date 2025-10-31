package com.vaxcare.unifiedhub.core.database.converter

import androidx.room.TypeConverter
import com.vaxcare.unifiedhub.core.database.model.enums.InventorySource

class InventorySourceEnumTypeConverters {
    @TypeConverter
    fun fromInventorySources(list: List<InventorySource>): String = list.map { it.id.toString() }.joinToString(",")

    @TypeConverter
    fun toInventorySources(string: String) = string.split(",").map { intToEnum(it.toInt(), InventorySource.PRIVATE) }

    @TypeConverter
    fun fromInventorySource(invSource: InventorySource): Int = invSource.id

    @TypeConverter
    fun toInventorySource(int: Int) = intToEnum(int, InventorySource.PRIVATE)
}

inline fun <reified T : Enum<T>> intToEnum(type: Int, unknown: T): T {
    val map = enumValues<T>().associateBy { t: T ->
        when (t) {
            is InventorySource -> t.id
            else -> t.ordinal
        }
    }
    return map[type] ?: unknown
}
