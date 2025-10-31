package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste

import androidx.compose.ui.text.AnnotatedString
import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.components.EditProductLotQuantityUi
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode

sealed class LogWasteIntent : UiIntent {
    object NavigateBackClicked : LogWasteIntent()

    object ConfirmWastedProducts : LogWasteIntent()

    object SearchLot : LogWasteIntent()

    data class BarcodeScanned(val barcode: ParsedBarcode) : LogWasteIntent()

    data class EnterLotQuantity(val lotNumber: String) : LogWasteIntent()

    data class LotQuantityEntered(val lotNumber: String, val quantity: Int) : LogWasteIntent()

    data class IncrementLotAmount(val lotNumber: String, val amount: Int = 1) : LogWasteIntent()

    data class DecrementLotAmount(val lotNumber: String, val amount: Int = 1) : LogWasteIntent()

    data class RemoveLot(val lotNumber: String) : LogWasteIntent()

    data class UndoLotRemoved(val lotNumber: String) : LogWasteIntent()

    object DiscardChangesConfirmed : LogWasteIntent()

    object CloseDialog : LogWasteIntent()
}

sealed class LogWasteEvent : UiEvent {
    data object GoBack : LogWasteEvent()

    data object GoToSummary : LogWasteEvent()

    data object GoToLotSearch : LogWasteEvent()
}

sealed class LogWasteDialog : DialogKey {
    object DiscardChanges : LogWasteDialog()

    object ExpiredProduct : LogWasteDialog()

    data class EnterQuantity(val lotNumber: String) : LogWasteDialog()

    data class WrongProduct(val errorMessage: AnnotatedString) : LogWasteDialog()
}

data class LogWasteState(
    val isLoading: Boolean = true,
    val wastedProductsUi: List<EditProductLotQuantityUi> = emptyList(),
    val total: Int = 0,
    val reason: LogWasteReason = LogWasteReason.OTHER,
    val stockUi: StockUi = StockUi.PRIVATE,
    val isInvalidProductScanned: Boolean = false,
    override val activeDialog: LogWasteDialog? = null
) : UiState, ActiveDialog
