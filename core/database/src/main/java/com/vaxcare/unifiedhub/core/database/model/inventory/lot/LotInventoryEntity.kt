package com.vaxcare.unifiedhub.core.database.model.inventory.lot

import androidx.room.Entity
import com.squareup.moshi.JsonClass

@Entity(
    tableName = "LotInventory",
    primaryKeys = ["inventorySource", "productId", "lotNumber"]
)
@JsonClass(generateAdapter = true)
data class LotInventoryEntity(
    val lotNumber: String,
    val onHand: Int,
    val inventorySource: Int,
    val productId: Int,
    val antigen: String,
    val inventoryGroup: String,
    val prettyName: String,
    val avgUsed: Double,
    val usedDoses: Double,
    var productStatus: Int = 1
)
