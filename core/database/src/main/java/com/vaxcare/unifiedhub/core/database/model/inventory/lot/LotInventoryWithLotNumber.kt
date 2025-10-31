package com.vaxcare.unifiedhub.core.database.model.inventory.lot

import androidx.room.Relation

data class LotInventoryWithLotNumber(
    val lotNumber: String,
    val onHand: Int,
    val inventorySource: Int,
    val productId: Int,
    val antigen: String,
    val inventoryGroup: String,
    val prettyName: String,
    val avgUsed: Double,
    val usedDoses: Double,
    @Relation(parentColumn = "lotNumber", entityColumn = "name")
    val lot: LotNumberEntity
)
