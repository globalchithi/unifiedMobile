package com.vaxcare.unifiedhub.feature.transactions.addpublic.model

import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.AddedLotInventoryUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.session.AddPublicSession

object ProductUiMapperTestData {
    val mockLotsExcludingDeleted = listOf<Lot>(
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
        ),
        Lot(
            lotNumber = "!@#$",
            productId = 14,
            expiration = null,
            salesProductId = 1
        ),
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
        ),
        Product(
            id = 14,
            antigen = "antigen14",
            prettyName = "prettyName14",
            presentation = Presentation.MULTI_DOSE_VIAL,
            lossFee = 99F,
            displayName = "",
            categoryId = 0,
            inventoryGroup = ""
        )
    )
    val sessionLotState: Map<String, AddPublicSession.LotState> = mapOf(
        "EFGH" to AddPublicSession.LotState(5, false),
        "1234" to AddPublicSession.LotState(3, false),
        "5678" to AddPublicSession.LotState(10, false),
        "ABCD" to AddPublicSession.LotState(5, true),
        "!@#$" to AddPublicSession.LotState(5, false),
    )
    val sessionProductState: Map<Int, AddPublicSession.ProductState> = mapOf(
        0 to AddPublicSession.ProductState(),
        1 to AddPublicSession.ProductState(),
        14 to AddPublicSession.ProductState(isDeleted = true),
    )
    val expected = listOf(
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
    )
}
