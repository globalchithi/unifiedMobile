package com.vaxcare.unifiedhub.feature.transactions.counts.submit

import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.model.inventory.LotInventory
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.model.LotState
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitState
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.model.ProductUi

internal object CountsSubmitTestData {
    val lotNumbers = listOf(
        "SAMPLE",
        "AMPLE",
        "EXAMPLE",
        "ANKLE",
        "BANGLE"
    )

    val productIds = listOf(
        111,
        222,
        333
    )

    val lotState = mapOf(
        lotNumbers[0] to LotState(delta = 5),
        lotNumbers[1] to LotState(delta = -10),
        lotNumbers[2] to LotState(delta = 5),
        lotNumbers[3] to LotState(delta = -2, isDeleted = true),
        lotNumbers[4] to LotState(delta = 5),
    )

    val inventory = listOf(
        LotInventory(
            lotNumber = "SAMPLE",
            onHand = 15,
            inventorySourceId = 2,
        ),
        LotInventory(
            lotNumber = "AMPLE",
            onHand = 20,
            inventorySourceId = 2,
        ),
        LotInventory(
            lotNumber = "EXAMPLE",
            onHand = 35,
            inventorySourceId = 2,
        ),
        LotInventory(
            lotNumber = "ANKLE",
            onHand = 10,
            inventorySourceId = 2,
        ),
    )

    val products = listOf(
        Product(
            id = 111,
            inventoryGroup = "invGroup1",
            antigen = "INFLUENZA",
            displayName = "Flu",
            presentation = Presentation.PREFILLED_SYRINGE,
            categoryId = 0,
            prettyName = "Flu",
            lossFee = 99.99F
        ),
        Product(
            id = 222,
            inventoryGroup = "invGroup2",
            antigen = "RSV",
            displayName = "Rsv pretty",
            presentation = Presentation.NASAL_SPRAY,
            categoryId = 0,
            prettyName = "Rsv pretty",
            lossFee = 50F,
        ),
        Product(
            id = 333,
            inventoryGroup = "invGroup3",
            antigen = "Varicella",
            displayName = "Varivax",
            presentation = Presentation.NASAL_SPRAY,
            categoryId = 0,
            prettyName = "Varivax",
            lossFee = 299.99F
        ),
    )

    val lots = listOf(
        Lot(
            lotNumber = lotNumbers[0],
            productId = productIds[0],
            expiration = null,
            salesProductId = productIds[0]
        ),
        Lot(
            lotNumber = lotNumbers[1],
            productId = productIds[0],
            expiration = null,
            salesProductId = productIds[0]
        ),
        Lot(
            lotNumber = lotNumbers[2],
            productId = productIds[1],
            expiration = null,
            salesProductId = productIds[1]
        ),
        Lot(
            lotNumber = lotNumbers[3],
            productId = productIds[2],
            expiration = null,
            salesProductId = productIds[2]
        ),
        Lot(
            lotNumber = lotNumbers[4],
            productId = productIds[1],
            expiration = null,
            salesProductId = productIds[1]
        ),
    )

    internal val defaultUiStatePublicStock = CountsSubmitState(
        stockType = StockUi.VFC,
        nonSeasonalProducts = listOf(
            ProductUi(
                id = 222,
                antigen = "RSV",
                prettyName = "Rsv pretty",
                quantity = 35,
                unitPrice = "$50.00",
                delta = 10,
                impact = "$500.00",
                presentationIcon = R.drawable.ic_presentation_nasal
            ),
        ),
        seasonalProducts = listOf(
            ProductUi(
                id = 111,
                antigen = "INFLUENZA",
                prettyName = "Flu",
                quantity = 35,
                unitPrice = "$99.99",
                delta = -5,
                impact = "-$499.95",
                presentationIcon = R.drawable.ic_presentation_syringe
            ),
        ),
        subTotal = "$0.05",
        showImpact = false
    )

    internal val defaultUiStatePrivateStock = CountsSubmitState(
        stockType = StockUi.PRIVATE,
        nonSeasonalProducts = listOf(
            ProductUi(
                id = 222,
                antigen = "RSV",
                prettyName = "Rsv pretty",
                quantity = 35,
                unitPrice = "$50.00",
                delta = 10,
                impact = "$500.00",
                presentationIcon = R.drawable.ic_presentation_nasal
            ),
        ),
        seasonalProducts = listOf(
            ProductUi(
                id = 111,
                antigen = "INFLUENZA",
                prettyName = "Flu",
                quantity = 35,
                unitPrice = "$99.99",
                delta = -5,
                impact = "-$499.95",
                presentationIcon = R.drawable.ic_presentation_syringe
            ),
        ),
        subTotal = "$0.05",
        showImpact = true
    )
}
