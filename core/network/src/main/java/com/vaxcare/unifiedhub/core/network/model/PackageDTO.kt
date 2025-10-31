package com.vaxcare.unifiedhub.core.network.model

data class PackageDTO(
    val id: Int,
    val description: String,
    val itemCount: Int,
    val packageNdcs: List<String>,
    val productId: Int,
    val salesProductId: Int
)
