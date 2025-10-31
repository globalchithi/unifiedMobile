package com.vaxcare.unifiedhub.core.database.model.inventory.lot

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vaxcare.unifiedhub.core.database.model.enums.InventorySource
import java.time.LocalDateTime

@Entity(tableName = "LotConfirmation")
data class LotConfirmationEntity(
    val onHand: Int,
    val confirmed: Int,
    val productId: Int,
    val inventorySource: InventorySource,
    val savedOn: LocalDateTime,
    @PrimaryKey
    val lotNumber: String
)
