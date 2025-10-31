package com.vaxcare.unifiedhub.feature.transactions.addpublic.complete

import com.vaxcare.unifiedhub.core.common.ext.toStandardDate
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.complete.AddPublicCompleteState
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.AddedLotInventoryUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUi
import java.time.LocalDate

object AddPublicCompleteTestData {
    object Initial {
        val expectedUiState = AddPublicCompleteState(
            stockType = StockUi.VFC,
            products = listOf(
                ProductUi(
                    id = 0,
                    inventory = listOf(
                        AddedLotInventoryUi(
                            lotNumber = "1234",
                            expiration = "",
                            count = 3,
                        ),
                        AddedLotInventoryUi(
                            lotNumber = "5678",
                            expiration = "",
                            count = 10,
                        ),
                    ),
                    antigen = "antigen",
                    prettyName = "prettyName",
                    cartonCount = 0,
                    presentation = Presentation.PREFILLED_SYRINGE,
                ),
                ProductUi(
                    id = 1,
                    inventory = listOf(
                        AddedLotInventoryUi(
                            lotNumber = "EFGH",
                            expiration = "",
                            count = 5,
                        ),
                    ),
                    antigen = "antigen2",
                    prettyName = "prettyName2",
                    cartonCount = 0,
                    presentation = Presentation.NASAL_SYRINGE
                )
            ),
            date = LocalDate.now().toStandardDate(),
            totalProducts = "18"
        )
    }
}
