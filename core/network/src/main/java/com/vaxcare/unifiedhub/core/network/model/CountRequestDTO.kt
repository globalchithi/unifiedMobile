package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class CountRequestDTO(
    @Json(name = "ClinicId") val clinicId: Long,
    @Json(name = "VaccineCountEntries") val vaccineCountEntries: List<CountEntryRequestDTO>,
    @Json(name = "CreatedOn") val createdOn: LocalDateTime = LocalDateTime.now(),
    @Json(name = "Guid") val guid: String,
    @Json(name = "Stock") val stock: String,
    @Json(name = "UserId") val userId: Int
)
