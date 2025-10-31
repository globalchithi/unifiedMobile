package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.data.model.inventory.enums.TransactionType
import com.vaxcare.unifiedhub.core.model.AdjustmentType
import com.vaxcare.unifiedhub.core.model.inventory.Adjustment
import com.vaxcare.unifiedhub.core.network.model.AdjustmentEntryRequestDTO
import javax.inject.Inject

class AdjustmentMapper @Inject constructor() {
    fun domainToNetwork(data: Adjustment): List<AdjustmentEntryRequestDTO> =
        with(data) {
            val transactionType = when (type) {
                AdjustmentType.ADD_PUBLIC -> TransactionType.RECEIVE_DELIVERY
                AdjustmentType.LOG_WASTE -> TransactionType.LOSS_WASTE
                AdjustmentType.BUYBACK -> TODO()
                AdjustmentType.RETURN -> TransactionType.RETURN
            }
            val reason = transactionType.label

            adjustments.map {
                AdjustmentEntryRequestDTO(
                    adjustmentReason = reason,
                    adjustmentReasonType = adjustmentReasonType,
                    adjustmentType = transactionType.id.toString(),
                    delta = it.delta,
                    doseValue = it.doseValue,
                    groupGuid = groupGuid,
                    lotExpirationDate = it.expiration,
                    lotNumber = it.lotNumber,
                    salesProductId = it.salesProductId,
                    receiptKey = it.receiptKey,
                    stock = stockId,
                    userId = userId,
                    userName = userName
                )
            }
        }
}
