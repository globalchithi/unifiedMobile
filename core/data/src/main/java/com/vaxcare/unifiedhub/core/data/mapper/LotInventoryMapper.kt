package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.database.model.inventory.lot.LotInventoryEntity
import com.vaxcare.unifiedhub.core.model.inventory.LotInventory
import com.vaxcare.unifiedhub.core.network.model.LotInventoryDTO
import javax.inject.Inject

class LotInventoryMapper @Inject constructor() {
    fun networkToEntity(data: List<LotInventoryDTO>) =
        data.map {
            LotInventoryEntity(
                lotNumber = it.lotNumber,
                onHand = it.onHand,
                inventorySource = it.inventorySource,
                productId = it.productId,
                antigen = it.antigen,
                inventoryGroup = it.inventoryGroup,
                prettyName = it.prettyName,
                avgUsed = it.avgUsed,
                usedDoses = it.usedDoses,
                productStatus = it.productStatus
            )
        }

    fun entityToDomain(data: LotInventoryEntity) =
        with(data) {
            LotInventory(
                lotNumber = lotNumber,
                inventorySourceId = inventorySource,
                onHand = onHand,
                inventoryGroup = inventoryGroup,
                antigen = antigen,
                productId = productId
            )
        }

    fun entityToDomain(data: List<LotInventoryEntity>) = data.map { entityToDomain(it) }
}
