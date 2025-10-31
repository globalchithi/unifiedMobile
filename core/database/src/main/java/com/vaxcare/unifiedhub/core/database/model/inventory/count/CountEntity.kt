package com.vaxcare.unifiedhub.core.database.model.inventory.count

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import com.vaxcare.unifiedhub.core.database.model.enums.InventorySource
import java.time.LocalDateTime

@Entity(tableName = "Count")
@JsonClass(generateAdapter = true)
data class CountEntity(
    @PrimaryKey val guid: String,
    val clinicId: Long,
    val createdOn: LocalDateTime,
    val stock: InventorySource,
    val userId: Int
)
