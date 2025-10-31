package com.vaxcare.unifiedhub.core.model.inventory

import com.vaxcare.unifiedhub.core.model.AdjustmentType

data class Adjustment(
    val key: String,
    val groupGuid: String,
    val adjustmentReasonType: String? = null,
    val type: AdjustmentType,
    val stockId: String,
    val adjustments: List<AdjustmentEntry>,
    val userId: Long,
    val userName: String
)

data class AdjustmentEntry(
    val lotNumber: String,
    val salesProductId: Int,
    val delta: Int,
    val doseValue: Float,
    val expiration: String,
    val receiptKey: String
)
