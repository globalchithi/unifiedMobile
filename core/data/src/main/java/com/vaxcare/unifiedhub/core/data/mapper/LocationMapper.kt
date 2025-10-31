package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.database.model.LocationEntity
import com.vaxcare.unifiedhub.core.database.model.enums.InventorySource
import com.vaxcare.unifiedhub.core.database.model.enums.InventorySource.*
import com.vaxcare.unifiedhub.core.model.Location
import com.vaxcare.unifiedhub.core.network.model.InventorySourceDTO
import com.vaxcare.unifiedhub.core.network.model.LocationNetworkDTO
import javax.inject.Inject

class LocationMapper @Inject constructor(
    private val stockTypeMapper: StockTypeMapper
) {
    fun networkToEntity(data: LocationNetworkDTO) =
        with(data) {
            LocationEntity(
                clinicId = clinicId,
                partnerId = partnerId,
                partnerName = partnerName,
                clinicNumber = clinicNumber,
                clinicName = clinicName,
                address = address,
                city = city,
                state = state,
                zipCode = zipCode,
                primaryPhone = primaryPhone,
                contactId = contactId,
                parentClinicId = parentClinicId,
                inventorySources = listOf(PRIVATE) + inventorySources.map { it.toInventorySource() },
            )
        }

    private fun InventorySourceDTO.toInventorySource(): InventorySource =
        when (id) {
            PRIVATE.id -> PRIVATE
            VFC.id -> VFC
            STATE.id -> STATE
            THREE_SEVENTEEN.id -> THREE_SEVENTEEN
            ANOTHER_LOCATION.id -> ANOTHER_LOCATION
            ANOTHER_STOCK.id -> ANOTHER_STOCK
            OTHER.id -> OTHER
            else -> PRIVATE
        }

    fun entityToDomain(data: LocationEntity?): Location? =
        if (data != null) {
            Location(
                clinicName = data.clinicName,
                partnerName = data.partnerName,
                stockTypes = data.inventorySources.map { stockTypeMapper.entityToDomain(it) },
            )
        } else {
            null
        }
}
