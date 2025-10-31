package com.vaxcare.unifiedhub.core.database.model.inventory.count

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CountEntry")
data class CountEntryEntity(
    @PrimaryKey val guid: String,
    val countGuid: String,
    val doseValue: Int,
    val epProductId: Int,
    val lotNumber: String,
    val newOnHand: Int,
    val prevOnHand: Int
)
