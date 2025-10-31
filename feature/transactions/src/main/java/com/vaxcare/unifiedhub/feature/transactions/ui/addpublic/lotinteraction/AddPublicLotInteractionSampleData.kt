package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction

import com.vaxcare.unifiedhub.core.common.ext.toLocalDateString
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.AddedLotInventoryUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUi
import java.time.LocalDate

object AddPublicLotInteractionSampleData {
    private val unexpiredDateStr = LocalDate
        .now()
        .plusYears(1)
        .toLocalDateString("MM/yyyy")

    val Default = AddPublicLotInteractionState(
        isScannerActive = false,
        stockType = StockUi.PRIVATE,
        product = ProductUi(
            id = 0,
            inventory = listOf(
                AddedLotInventoryUi(
                    lotNumber = "TESTRJK498",
                    expiration = unexpiredDateStr,
                    count = 9
                ),
                AddedLotInventoryUi(
                    lotNumber = "TLSK498",
                    expiration = unexpiredDateStr,
                    count = 4
                ),
                AddedLotInventoryUi(
                    lotNumber = "TLLKSJK99",
                    expiration = unexpiredDateStr,
                    isDeleted = true,
                    count = 2
                ),
                AddedLotInventoryUi(
                    lotNumber = "TLLKSJK10",
                    expiration = "",
                    isDeleted = false,
                    count = 2
                ),
            ),
            cartonCount = 20,
            antigen = "ANTIGEN",
            prettyName = "Antigen",
            presentation = Presentation.NASAL_SPRAY
        ),
    )
}
