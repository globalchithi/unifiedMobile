package com.vaxcare.unifiedhub.core.model.lot

import java.time.LocalDate

data class Lot(
    val lotNumber: String,
    val productId: Int,
    val expiration: LocalDate?,
    var salesProductId: Int
)
