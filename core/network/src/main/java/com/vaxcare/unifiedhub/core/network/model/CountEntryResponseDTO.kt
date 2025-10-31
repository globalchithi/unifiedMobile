package com.vaxcare.unifiedhub.core.network.model

data class CountEntryResponseDTO(
    val guid: String,
    val countGuid: String,
    val doseValue: Int,
    val epProductId: Int,
    val lotNumber: String,
    val newOnHand: Int,
    val prevOnHand: Int
)
