package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class LotInventoryDTO(
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

@Deprecated("Do not use this")
data class LotNumberWithProductDTO(
    val expirationDate: LocalDate?,
    val id: Int,
    val name: String,
    val productId: Int,
    val salesLotNumberId: Int,
    val salesProductId: Int,
    val product: ProductDTO
)
