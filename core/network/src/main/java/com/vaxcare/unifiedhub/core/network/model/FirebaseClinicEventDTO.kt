package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FirebaseClinicEventDTO(
    @Json(name = "PartnerId") val partnerId: Int?,
    @Json(name = "ParentClinicId") val parentClinicId: Int?,
    @Json(name = "ClinicId") val clinicId: Int?,
    @Json(name = "EventId") val eventId: String?,
    @Json(name = "ChangedFeatureFlagIds") val changedFeatureFlagIds: List<Int>?,
)
