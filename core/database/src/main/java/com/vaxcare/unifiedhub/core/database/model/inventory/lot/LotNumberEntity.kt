package com.vaxcare.unifiedhub.core.database.model.inventory.lot

import androidx.room.Entity
import com.squareup.moshi.JsonClass
import java.time.LocalDate

@Entity(
    tableName = "LotNumber",
    primaryKeys = ["lotNumber", "productId"]
)
@JsonClass(generateAdapter = true)
data class LotNumberEntity(
    var expirationDate: LocalDate?,
    val id: Int,
    val lotNumber: String,
    val productId: Int,
    val salesLotNumberId: Int,
    val salesProductId: Int,
    val unreviewed: Boolean,
    val source: Int?
)
