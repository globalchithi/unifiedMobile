package com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction

import androidx.compose.ui.text.AnnotatedString
import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductLotUi
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason.ReturnReasonUi
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode

sealed interface ReturnsProductInteractionIntent : UiIntent {
    data object CloseScreen : ReturnsProductInteractionIntent

    data object DismissDialog : ReturnsProductInteractionIntent

    data object ConfirmDiscardChanges : ReturnsProductInteractionIntent

    data object GoForward : ReturnsProductInteractionIntent

    data object SearchLot : ReturnsProductInteractionIntent

    data class UpdateLotCount(
        val lotNumber: String,
        val change: Int
    ) : ReturnsProductInteractionIntent

    data class SubmitKeypadInput(
        val lotNumber: String,
        val count: Int
    ) : ReturnsProductInteractionIntent

    data class OpenKeypad(val lotNumber: String) : ReturnsProductInteractionIntent

    data class DeleteLot(val lotNumber: String) : ReturnsProductInteractionIntent

    data class UndoDelete(val lotNumber: String) : ReturnsProductInteractionIntent

    data class ScanLot(val barcode: ParsedBarcode) : ReturnsProductInteractionIntent
}

sealed interface ReturnsProductInteractionEvent : UiEvent {
    data object NavigateBack : ReturnsProductInteractionEvent

    data class NavigateToLotSearch(val sourceId: Int) : ReturnsProductInteractionEvent

    data object NextScreen : ReturnsProductInteractionEvent
}

data class ReturnsProductInteractionState(
    val isLoading: Boolean = true,
    val isScannerActive: Boolean = false,
    val lots: List<ProductLotUi> = emptyList(),
    val reason: ReturnReasonUi? = null,
    val showScanError: Boolean = false,
    val error: ReturnsProductInteractionError? = null,
    override val activeDialog: DialogKey? = null,
) : UiState,
    ActiveDialog {
    val total: Int
        get() = lots
            .filter { !it.isDeleted }
            .sumOf { it.quantity }
}

sealed interface ReturnsProductInteractionDialog : DialogKey {
    data class Keypad(val lotName: String) : ReturnsProductInteractionDialog

    data object DiscardChanges : ReturnsProductInteractionDialog

    data class WrongProduct(val errorMessage: AnnotatedString) : ReturnsProductInteractionDialog
}

sealed interface ReturnsProductInteractionError {
    data object BadBarcodeScan : ReturnsProductInteractionError
}
