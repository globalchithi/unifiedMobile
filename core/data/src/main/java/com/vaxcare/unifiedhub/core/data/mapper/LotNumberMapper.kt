package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.database.model.inventory.lot.LotNumberEntity
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.network.model.LotNumberDTO
import javax.inject.Inject

class LotNumberMapper @Inject constructor() {
    fun networkToEntity(data: List<LotNumberDTO>) = data.map { networkToEntity(it) }

    fun networkToEntity(data: LotNumberDTO) =
        LotNumberEntity(
            expirationDate = data.expirationDate,
            id = data.id,
            lotNumber = data.qualifiedLotNumber,
            productId = data.epProductId,
            salesLotNumberId = data.salesLotNumberId,
            salesProductId = data.salesProductId,
            unreviewed = data.unreviewed,
            source = data.source
        )

    fun entityToNetwork(data: List<LotNumberEntity>) =
        data.map {
            LotNumberDTO(
                expirationDate = it.expirationDate,
                id = it.id,
                qualifiedLotNumber = it.lotNumber,
                epProductId = it.productId,
                salesLotNumberId = it.salesLotNumberId,
                salesProductId = it.salesProductId,
                unreviewed = it.unreviewed,
                source = it.source
            )
        }

    fun entityToDomain(data: List<LotNumberEntity>) = data.mapNotNull { entityToDomain(it) }

    fun entityToDomain(data: LotNumberEntity?) =
        with(data) {
            if (this == null) return@with null
            Lot(
                lotNumber = lotNumber,
                productId = productId,
                expiration = expirationDate,
                salesProductId = salesProductId
            )
        }
}
