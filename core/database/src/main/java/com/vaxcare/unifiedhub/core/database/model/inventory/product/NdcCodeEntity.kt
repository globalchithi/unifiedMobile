package com.vaxcare.unifiedhub.core.database.model.inventory.product

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NdcCode")
data class NdcCodeEntity(
    @PrimaryKey val ndcCode: String,
    val productId: Int
)
