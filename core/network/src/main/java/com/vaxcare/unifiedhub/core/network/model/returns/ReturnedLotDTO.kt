package com.vaxcare.unifiedhub.core.network.model.returns

import java.time.LocalDate

data class ReturnedLotDTO(
    val productId: Int,
    val lotNumber: String,
    val count: Int,
    val lotExpirationDate: LocalDate,
    val receiptKey: String,
)
