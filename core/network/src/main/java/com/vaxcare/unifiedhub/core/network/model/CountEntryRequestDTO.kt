package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.util.UUID

@JsonClass(generateAdapter = true)
data class CountEntryRequestDTO(
    @Json(name = "CountGuid") val countGuid: String,
    @Json(name = "EpProductId") val epProductId: Int,
    @Json(name = "EntryId") val entryId: String = UUID.randomUUID().toString(),
    @Json(name = "IsDirty") val isDirty: Int = 1,
    @Json(name = "NewOnHand") val newOnHand: Int,
    @Json(name = "PrevOnHand") val prevOnHand: Int,
    @Json(name = "AdjustmentType") val adjustmentType: String,
    @Json(name = "Delta") val delta: Int,
    @Json(name = "DoseValue") val doseValue: Float,
    @Json(name = "GroupGuid") val groupGuid: String,
    @Json(name = "LotExpirationDate") val lotExpirationDate: LocalDate,
    @Json(name = "LotNumber") val lotNumber: String,
    @Json(name = "ProductId") val productId: Int,
    @Json(name = "Stock") val stock: String,
    @Json(name = "UserId") val userId: Int,
    @Json(name = "UserName") val userName: String
)
