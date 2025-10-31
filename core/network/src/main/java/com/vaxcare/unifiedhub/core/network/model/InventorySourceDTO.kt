package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InventorySourceDTO(
    val name: String,
    val id: Int
)
