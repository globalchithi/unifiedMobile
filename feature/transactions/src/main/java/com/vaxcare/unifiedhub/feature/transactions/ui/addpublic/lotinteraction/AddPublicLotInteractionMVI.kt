package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction

import androidx.compose.ui.text.AnnotatedString
import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUi
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode

sealed class AddPublicLotInteractionIntent : UiIntent {
    data object CloseScreen : AddPublicLotInteractionIntent()

    data object DismissDialog : AddPublicLotInteractionIntent()

    data object DiscardChanges : AddPublicLotInteractionIntent()

    data object Confirm : AddPublicLotInteractionIntent()

    data object SearchLot : AddPublicLotInteractionIntent()

    data class UpdateLotCount(
        val lotNumber: String,
        val change: Int
    ) : AddPublicLotInteractionIntent()

    data class SubmitKeypadInput(
        val lotNumber: String,
        val count: Int
    ) : AddPublicLotInteractionIntent()

    data class OpenKeypad(
        val lotNumber: String
    ) : AddPublicLotInteractionIntent()

    data class DeleteLot(
        val lotNumber: String,
    ) : AddPublicLotInteractionIntent()

    data class UndoDelete(
        val lotNumber: String
    ) : AddPublicLotInteractionIntent()

    /**
     * The scanner detected and parsed a barcode on the camera preview view
     *
     * @property barcode barcode parsed - should be TwoDeeBarcode if valid scan
     */
    data class ScanLot(
        val barcode: ParsedBarcode
    ) : AddPublicLotInteractionIntent()
}

data class AddPublicLotInteractionState(
    val stockType: StockUi = StockUi.PRIVATE,
    val product: ProductUi? = null,
    val highlightedLot: String? = null,
    val isScannerActive: Boolean = false,
    val error: AddPublicLotInteractionError? = null,
    override val activeDialog: DialogKey? = null,
) : UiState,
    ActiveDialog

sealed class AddPublicLotInteractionDialog : DialogKey {
    data class Keypad(val lotName: String) : AddPublicLotInteractionDialog()

    data object DiscardChanges : AddPublicLotInteractionDialog()

    data object MismatchedProduct : AddPublicLotInteractionDialog()

    data class WrongProduct(val errorMessage: AnnotatedString) : AddPublicLotInteractionDialog()

    data object ExpiredDose : AddPublicLotInteractionDialog()
}

sealed class AddPublicLotInteractionEvent : UiEvent {
    data object NavigateBack : AddPublicLotInteractionEvent()

    data class NavigateToLotSearch(
        val filterProductId: Int?,
        val sourceId: Int
    ) : AddPublicLotInteractionEvent()
}

sealed class AddPublicLotInteractionError {
    data object BadBarcodeScan : AddPublicLotInteractionError()
}
