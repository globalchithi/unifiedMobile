package com.vaxcare.unifiedhub.core.model

import com.vaxcare.unifiedhub.core.model.inventory.StockType

data class Location(
    val clinicName: String?,
    val partnerName: String?,
    val stockTypes: List<StockType>
)
