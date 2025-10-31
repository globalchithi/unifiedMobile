package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class LotNumberDTO(
    var expirationDate: LocalDate?,
    val id: Int,
    val qualifiedLotNumber: String,
    val epProductId: Int,
    val salesLotNumberId: Int,
    val salesProductId: Int,
    val unreviewed: Boolean,
    val source: Int?
)
