package com.vaxcare.unifiedhub.feature.transactions.returns.productInteraction

import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.feature.transactions.returns.ReturnsSharedTestData
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionError
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionState
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason.ReturnReasonUi
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.session.ReturnsSession
import com.vaxcare.unifiedhub.library.scanner.domain.TwoDeeBarcode
import java.time.LocalDate

object ReturnsProductInteractionTestData {
    object Init {
        val uiState = ReturnsProductInteractionState(
            isLoading = false,
            isScannerActive = true,
            reason = ReturnReasonUi.EXCESS_INVENTORY,
        )
    }

    object InitExpired {
        val expectedUiState = ReturnsProductInteractionState(
            isLoading = false,
            isScannerActive = true,
            lots = ReturnsSharedTestData.mockProductLotsUi,
            reason = ReturnReasonUi.EXPIRED,
        )
    }

    object LotStateEmission {
        val newLotState = mapOf<String, ReturnsSession.LotState>(
            "VXC01" to ReturnsSession.LotState(count = 5),
        )
        val expectedUiState = Init.uiState
    }

    object DiscardChanges {
        val initialState = Init.uiState.copy(activeDialog = ReturnsProductInteractionDialog.DiscardChanges)
    }

    object DismissDialog {
        val initialState = Init.uiState.copy(
            activeDialog = ReturnsProductInteractionDialog.DiscardChanges
        )

        val expectedUiState = initialState.copy(
            activeDialog = null
        )
    }

    object SearchLot {
        val expectedEvent = ReturnsProductInteractionEvent.NavigateToLotSearch(
            sourceId = StockType.PRIVATE.id
        )
    }

    object UpdateLotCount {
        val intentToHandle = ReturnsProductInteractionIntent.UpdateLotCount(
            lotNumber = "VXC01",
            change = 5
        )
    }

    object OpenKeypad {
        val intentToHandle = ReturnsProductInteractionIntent.OpenKeypad("VXC01")
        val expectedUiState = Init.uiState.copy(
            isScannerActive = false,
            activeDialog = ReturnsProductInteractionDialog.Keypad("VXC01")
        )
    }

    object SubmitKeypadInput {
        val initialUiState = OpenKeypad.expectedUiState
        val intentToHandle = ReturnsProductInteractionIntent.SubmitKeypadInput("VXC01", 99)
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
        val intentToHandle = ReturnsProductInteractionIntent.ScanLot(mockBarcode)

        val valid = ScanValidationResult.Valid("VXC01", 1)
        val expired = ScanValidationResult.Expired("VXC01")
        val newLot = ScanValidationResult.NewLot(
            lotNumber = "VXC01",
            productId = 1,
            expiration = LocalDate.of(2026, 6, 1)
        )
        val duplicateLot = ScanValidationResult.DuplicateLot("VXC01")
        val duplicateLotExpectedUiState = Init.uiState.copy(
            isScannerActive = true,
            error = null
        )
        val invalidExpectedUiState = Init.uiState.copy(
            isScannerActive = true,
            activeDialog = null,
            error = ReturnsProductInteractionError.BadBarcodeScan
        )
    }
}
