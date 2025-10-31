package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class LotInventoryResponseDTO(
    val inventory: List<LotInventoryDTO>,
    val lastSync: LocalDateTime,
    val nextSync: LocalDateTime
)
