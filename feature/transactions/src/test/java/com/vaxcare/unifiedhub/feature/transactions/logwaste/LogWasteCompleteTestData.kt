package com.vaxcare.unifiedhub.feature.transactions.logwaste

import com.vaxcare.unifiedhub.core.common.ext.toStandardDate
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.model.LotState
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.complete.LogWasteCompleteState
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.model.ProductUi
import java.time.LocalDate
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

object LogWasteCompleteTestData {
    val mockLots = listOf<Lot>(
        Lot(
            lotNumber = "EFGH",
            productId = 1,
            expiration = null,
            salesProductId = 1
        ),
        Lot(
            lotNumber = "1234",
            productId = 0,
            expiration = null,
            salesProductId = 1
        ),
        Lot(
            lotNumber = "5678",
            productId = 0,
            expiration = null,
            salesProductId = 1
        )
    )
    val mockProducts = listOf<Product>(
        Product(
            id = 1,
            antigen = "antigen2",
            prettyName = "prettyName2",
            presentation = Presentation.NASAL_SYRINGE,
            lossFee = 20F,
            displayName = "",
            categoryId = 0,
            inventoryGroup = ""
        ),
        Product(
            id = 0,
            antigen = "antigen",
            prettyName = "prettyName",
            presentation = Presentation.PREFILLED_SYRINGE,
            lossFee = 10F,
            displayName = "",
            categoryId = 0,
            inventoryGroup = ""
        )
    )
    val sessionLotState: Map<String, LotState> = mapOf(
        "EFGH" to LotState(5, false),
        "1234" to LotState(3, false),
        "5678" to LotState(10, false),
        "ABCD" to LotState(5, true),
    )

    object InitialPrivate {
        val expectedUiState = LogWasteCompleteState(
            stockType = StockUi.PRIVATE,
            products = listOf(
                ProductUi(
                    id = 0,
                    antigen = "antigen",
                    prettyName = "prettyName",
                    quantity = 13,
                    unitPrice = "$10.00 ea.",
                    value = "$130.00",
                    valueFloat = 130f,
                    lotsPreview = "LOT# 1234 & 1 more",
                    presentationIcon = DesignSystemR.drawable.ic_presentation_syringe
                ),
                ProductUi(
                    id = 1,
                    antigen = "antigen2",
                    prettyName = "prettyName2",
                    quantity = 5,
                    unitPrice = "$20.00 ea.",
                    value = "$100.00",
                    valueFloat = 100f,
                    lotsPreview = "LOT# EFGH",
                    presentationIcon = DesignSystemR.drawable.ic_presentation_nasal
                )
            ),
            date = LocalDate.now().toStandardDate(),
            showImpact = true,
            totalImpact = "-$230.00"
        )
    }

    object InitialVfc {
        val expectedUiState = LogWasteCompleteState(
            stockType = StockUi.VFC,
            products = listOf(
                ProductUi(
                    id = 0,
                    antigen = "antigen",
                    prettyName = "prettyName",
                    quantity = 13,
                    unitPrice = "",
                    value = "13.0",
                    valueFloat = 13F,
                    lotsPreview = "LOT# 1234 & 1 more",
                    presentationIcon = DesignSystemR.drawable.ic_presentation_syringe
                ),
                ProductUi(
                    id = 1,
                    antigen = "antigen2",
                    prettyName = "prettyName2",
                    quantity = 5,
                    unitPrice = "",
                    value = "5.0",
                    valueFloat = 5F,
                    lotsPreview = "LOT# EFGH",
                    presentationIcon = DesignSystemR.drawable.ic_presentation_nasal
                )
            ),
            date = LocalDate.now().toStandardDate(),
            showImpact = false,
            totalProducts = "18"
        )
    }
}
