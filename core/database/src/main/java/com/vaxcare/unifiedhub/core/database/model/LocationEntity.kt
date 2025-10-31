package com.vaxcare.unifiedhub.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vaxcare.unifiedhub.core.database.model.enums.InventorySource

@Entity(tableName = "Location")
data class LocationEntity(
    @PrimaryKey
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
    val inventorySources: List<InventorySource>
)
