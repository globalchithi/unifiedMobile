package com.vaxcare.unifiedhub.feature.transactions.addpublic.lotinteraction

import androidx.compose.ui.text.AnnotatedString
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction.AddPublicLotInteractionError
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction.AddPublicLotInteractionEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction.AddPublicLotInteractionIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction.AddPublicLotInteractionState
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.AddedLotInventoryUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.session.AddPublicSession
import com.vaxcare.unifiedhub.library.scanner.domain.TwoDeeBarcode
import java.time.LocalDate
import com.vaxcare.unifiedhub.core.model.product.Package as Pkg
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction.AddPublicLotInteractionDialog as Dialog

object AddPublicLotInteractionTestData {
    object Init {
        val pkg = Pkg(
            id = 1,
            productId = 123,
            itemCount = 5
        )
        val product = Product(
            id = 1,
            antigen = "ANTIGEN",
            displayName = "Display Name",
            presentation = Presentation.PREFILLED_SYRINGE,
            categoryId = 0,
            prettyName = "Pretty Name",
            lossFee = null,
            inventoryGroup = "null"
        )
        val lots = listOf(
            Lot(
                lotNumber = "ANTIGE",
                productId = 1,
                expiration = LocalDate.of(2026, 6, 30),
                salesProductId = 1
            ),
            Lot(
                lotNumber = "INFLUE",
                productId = 1,
                expiration = LocalDate.of(2026, 6, 30),
                salesProductId = 1
            ),
        )
        val lotState = mapOf(
            "ANTIGE" to AddPublicSession.LotState(
                count = 1
            )
        )
        val uiState = AddPublicLotInteractionState(
            stockType = StockUi.VFC,
            product = ProductUi(
                id = 1,
                inventory = listOf(
                    AddedLotInventoryUi(
                        lotNumber = "ANTIGE",
                        expiration = "06/2026",
                        count = 1,
                    )
                ),
                cartonCount = 5,
                antigen = "ANTIGEN",
                prettyName = "Pretty Name",
                isDeleted = false,
                presentation = Presentation.PREFILLED_SYRINGE
            ),
            isScannerActive = true
        )
    }

    object DismissDialog {
        val initialState = Init.uiState.copy(
            activeDialog = Dialog.DiscardChanges
        )

        val expectedUiState = initialState.copy(
            activeDialog = null
        )
    }

    object DiscardChanges {
        val initialState = Init.uiState.copy(activeDialog = Dialog.DiscardChanges)
    }

    object SearchLot {
        val expectedEvent = AddPublicLotInteractionEvent.NavigateToLotSearch(
            filterProductId = 1,
            sourceId = StockType.VFC.id
        )
    }

    object LotStateEmission {
        val newLotState = mapOf("ANTIGE" to AddPublicSession.LotState(count = 6))
        val expectedUiState = Init.uiState.copy(
            product = Init.uiState.product?.copy(
                inventory = listOf(
                    AddedLotInventoryUi(
                        lotNumber = "ANTIGE",
                        expiration = "06/2026",
                        count = 6,
                    )
                )
            )
        )
    }

    object UpdateLotCount {
        val intentToHandle = AddPublicLotInteractionIntent.UpdateLotCount(
            lotNumber = "ANTIGE",
            change = 5
        )
    }

    object OpenKeypad {
        val intentToHandle = AddPublicLotInteractionIntent.OpenKeypad("ANTIGE")
        val expectedUiState = Init.uiState.copy(
            isScannerActive = false,
            activeDialog = Dialog.Keypad("ANTIGE")
        )
    }

    object SubmitKeypadInput {
        val initialUiState = OpenKeypad.expectedUiState
        val intentToHandle = AddPublicLotInteractionIntent.SubmitKeypadInput("ANTIGE", 99)
        val expectedUiState = initialUiState.copy(
            isScannerActive = true,
            activeDialog = null
        )
    }

    object ScanValidation {
        private val mockBarcode = TwoDeeBarcode(
            raw = "",
            symbologyName = "",
            vialNdc = "",
            lotNumber = "",
            expiration = null
        )
        val intentToHandle = AddPublicLotInteractionIntent.ScanLot(mockBarcode)

        val valid = ScanValidationResult.Valid("ABCDEF", 1)
        val newLot = ScanValidationResult.NewLot(
            lotNumber = "ABCDEF",
            productId = 1,
            expiration = LocalDate.of(2026, 6, 1)
        )
        val duplicateLot = ScanValidationResult.DuplicateLot("ABCDEF")
        val duplicateLotExpectedUiState = Init.uiState.copy(
            isScannerActive = true,
            highlightedLot = "ABCDEF",
            error = null
        )
        val wrongProduct = ScanValidationResult.WrongProduct("no do that")
        val wrongProductExpectedUiState = Init.uiState.copy(
            isScannerActive = false,
            activeDialog = Dialog.WrongProduct(AnnotatedString("no do that")),
            error = null
        )
        val expiredExpectedUiState = Init.uiState.copy(
            isScannerActive = false,
            activeDialog = Dialog.ExpiredDose,
            error = null
        )
        val mismatchedExpectedUiState = Init.uiState.copy(
            isScannerActive = false,
            activeDialog = Dialog.MismatchedProduct,
            error = null
        )
        val invalidExpectedUiState = Init.uiState.copy(
            isScannerActive = true,
            activeDialog = null,
            error = AddPublicLotInteractionError.BadBarcodeScan
        )
    }
}
