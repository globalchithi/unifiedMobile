package com.vaxcare.unifiedhub.feature.transactions.lot.functionality

import com.vaxcare.unifiedhub.core.common.ext.toLocalDateString
import com.vaxcare.unifiedhub.core.data.extension.toShorthand
import com.vaxcare.unifiedhub.core.model.inventory.LotInventory
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.LotInteractionState
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.LotInventoryUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.ProductUi
import java.time.LocalDate
import com.vaxcare.unifiedhub.core.model.product.Package as Pkg

val testProduct = Product(
    id = 123,
    inventoryGroup = "invGroup1",
    antigen = "RSV",
    displayName = "Rsv name",
    presentation = Presentation.SINGLE_DOSE_TUBE,
    categoryId = 2,
    prettyName = "RsV PrEttY NAmE",
    lossFee = 99.99F
)

val lotToAdd = Lot(
    lotNumber = "123test2",
    productId = 123,
    expiration = LocalDate.now().plusYears(1),
    salesProductId = 123
)
val lotUiToAdd = LotInventoryUi(
    lotNumber = "123test2",
    onHand = 0,
    adjustment = 0,
    expiration = LocalDate
        .now()
        .plusYears(1)
        .toLocalDateString("MM/yyyy")
)

val testLotInventory = listOf(
    LotInventory(
        lotNumber = "123test1",
        onHand = 5,
        inventorySourceId = 1
    )
)

val testLots = listOf(
    Lot(
        lotNumber = "123test1",
        productId = 123,
        expiration = LocalDate
            .now()
            .plusYears(1),
        salesProductId = 123
    )
)

val initialTestLotUiItems = listOf(
    LotInventoryUi(
        lotNumber = "123test1",
        onHand = 5,
        expiration = LocalDate
            .now()
            .plusYears(1)
            .toLocalDateString("MM/yyyy"),
    )
)

val initialTestProductUiItem = ProductUi(
    id = 123,
    antigen = "RSV",
    presentation = Presentation.SINGLE_DOSE_TUBE,
    prettyName = "RsV PrEttY NAmE"
)

val testPackage = Pkg(
    id = 1,
    productId = 123,
    itemCount = 5
)

var initialState = LotInteractionState(
    stockType = StockUi.PRIVATE,
    isScannerActive = true,
    prettyName = initialTestProductUiItem.prettyName,
    antigen = initialTestProductUiItem.antigen,
    lots = initialTestLotUiItems,
    presentation = initialTestProductUiItem.presentation,
    lotCountTotal = initialTestLotUiItems.sumOf { it.onHand }.toShorthand(),
    cartonCount = testPackage.itemCount
)
