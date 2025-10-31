package com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction

import com.vaxcare.unifiedhub.core.common.ext.toLocalDateString
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.LotInventoryUi
import java.time.LocalDate

object LotInteractionSampleData {
    private val unexpiredDateStr = LocalDate
        .now()
        .plusYears(1)
        .toLocalDateString("MM/yyyy")

    private val expiredDateStr = LocalDate
        .now()
        .minusYears(1)
        .toLocalDateString("MM/dd/yyyy")

    val Default = LotInteractionState(
        isScannerActive = false,
        stockType = StockUi.PRIVATE,
        lots = listOf(
            LotInventoryUi(
                lotNumber = "TESTRJK498",
                expiration = unexpiredDateStr,
                onHand = 0,
                adjustment = 3,
            ),
            LotInventoryUi(
                lotNumber = "TLSK498",
                expiration = unexpiredDateStr,
                onHand = 0,
                adjustment = -1,
                isActionRequired = true
            ),
            LotInventoryUi(
                lotNumber = "TLLKSJK98",
                expiration = unexpiredDateStr,
                onHand = 0,
                adjustment = 0,
                isDeleted = true
            ),
            LotInventoryUi(
                lotNumber = "123456",
                expiration = unexpiredDateStr,
                onHand = 0,
                adjustment = 0,
            ),
            LotInventoryUi(
                lotNumber = "GH839O",
                expiration = unexpiredDateStr,
                onHand = 8,
                isDeleted = true
            ),
            LotInventoryUi(
                lotNumber = "RJK498",
                expiration = expiredDateStr,
                onHand = 3,
                adjustment = 5,
                isExpired = true
            ),
            LotInventoryUi(
                lotNumber = "LOTWITH_ACTIONREQ",
                expiration = unexpiredDateStr,
                onHand = -5,
                isExpired = false,
                isActionRequired = true
            ),
        ),
        isActionRequired = true,
        antigen = "RSV",
        prettyName = "Abrysvo",
        cartonCount = 1,
        presentation = Presentation.PREFILLED_SYRINGE,
        lotCountTotal = "1.23k"
    )
}
