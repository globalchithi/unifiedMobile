package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationNetworkDTO(
    val clinicId: Int,
    val partnerId: Int,
    val partnerName: String?,
    val clinicNumber: String?,
    val clinicName: String?,
    val address: String?,
    val city: String?,
    val state: String?,
    val zipCode: String?,
    val primaryPhone: String?,
    val contactId: Int?,
    val parentClinicId: Int?,
    val inventorySources: List<InventorySourceDTO>,
    // TODO: not implemented
    var activeFeatureFlags: List<FeatureFlagDTO>,
)
