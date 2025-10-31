package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.database.model.inventory.count.CountEntryEntity
import com.vaxcare.unifiedhub.core.network.model.CountResponseDTO
import javax.inject.Inject

class CountEntryMapper @Inject constructor() {
    fun networkToEntity(data: List<CountResponseDTO>) =
        data.flatMap { count ->
            count.countList.map { entry ->
                CountEntryEntity(
                    countGuid = entry.countGuid,
                    doseValue = entry.doseValue,
                    epProductId = entry.epProductId,
                    guid = entry.guid,
                    lotNumber = entry.lotNumber,
                    newOnHand = entry.newOnHand,
                    prevOnHand = entry.prevOnHand
                )
            }
        }
}
