package com.vaxcare.unifiedhub.core.network.model.returns

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class ReturnDTO(
    val boxes: Int,
    val pickupTime: LocalDateTime,
    val groupGuid: String,
    val returnReason: Int,
    val stock: Int,
    val userName: String,
    val userId: Int,
    val lots: List<ReturnedLotDTO>
)
