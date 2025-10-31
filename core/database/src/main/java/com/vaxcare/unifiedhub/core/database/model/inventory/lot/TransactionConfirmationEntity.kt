package com.vaxcare.unifiedhub.core.database.model.inventory.lot

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vaxcare.unifiedhub.core.database.model.enums.InventorySource
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "TransactionConfirmation")
data class TransactionConfirmationEntity(
    val onHand: Int,
    val savedDelta: Int,
    val productId: Int,
    val inventorySource: InventorySource,
    val savedOn: LocalDateTime,
    @PrimaryKey
    val lotNumber: String,
    val transactionType: Int? = null,
    val doseValue: Int? = null,
    val salesProductId: Int? = null,
    val expDate: LocalDate? = null
)
