package com.vaxcare.unifiedhub.core.model.inventory

import com.vaxcare.unifiedhub.core.model.PickupAvailability
import java.time.LocalDate

data class Return(
    val groupGuid: String,
    val stockId: Int,
    val reasonId: Int,
    val pickup: PickupAvailability?,
    val noOfLabels: Int?,
    val returnedLots: List<ReturnedLot>,
    val userId: Long,
    val userName: String
)

data class ReturnedLot(
    val receiptKey: String,
    val productId: Int,
    val lotNumber: String,
    val count: Int,
    val expirationDate: LocalDate,
)
