package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.model.inventory.Return
import com.vaxcare.unifiedhub.core.network.model.returns.ReturnDTO
import com.vaxcare.unifiedhub.core.network.model.returns.ReturnedLotDTO
import timber.log.Timber
import javax.inject.Inject

class ReturnMapper @Inject constructor() {
    fun domainToNetwork(data: Return): ReturnDTO? {
        return with(data) {
            val pickupTime = pickup?.let {
                it.date.atTime(it.startTime)
            } ?: run {
                Timber.e("Impossible state reached: pickup time was `null` when submitting a Private return.")
                return@with null
            }

            ReturnDTO(
                boxes = noOfLabels ?: 0,
                pickupTime = pickupTime,
                groupGuid = groupGuid,
                returnReason = reasonId,
                stock = stockId,
                userName = userName,
                userId = userId.toInt(),
                lots = returnedLots.map {
                    ReturnedLotDTO(
                        productId = it.productId,
                        lotNumber = it.lotNumber,
                        count = it.count,
                        lotExpirationDate = it.expirationDate,
                        receiptKey = it.receiptKey
                    )
                }
            )
        }
    }
}
