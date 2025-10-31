package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AdjustmentEntryRequestDTO(
    val adjustmentReason: String,
    val adjustmentReasonType: String?,
    val adjustmentType: String,
    val delta: Int,
    val doseValue: Float,
    val groupGuid: String,
    val isLotManuallyEntered: Int = 0,
    val lotExpirationDate: String,
    val lotNumber: String,
    @Json(name = "productId") val salesProductId: Int,
    val receiptKey: String,
    val stock: String,
    val userId: Long,
    val userName: String
)
