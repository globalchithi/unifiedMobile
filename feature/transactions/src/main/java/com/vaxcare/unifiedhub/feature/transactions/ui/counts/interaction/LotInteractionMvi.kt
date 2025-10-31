package com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.LotInventoryUi
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode

sealed class LotInteractionIntent : UiIntent {
    data object CloseScreen : LotInteractionIntent()

    data object CloseCurrentDialog : LotInteractionIntent()

    data object ConfirmDiscardChanges : LotInteractionIntent()

    data object ConfirmLotInventory : LotInteractionIntent()

    data object SearchLot : LotInteractionIntent()

    data class UpdateLotDelta(
        val lotInventory: LotInventoryUi,
        val delta: Int
    ) : LotInteractionIntent()

    data class NumPadEntry(
        val lotNumber: String,
        val delta: Int
    ) : LotInteractionIntent()

    data class OpenNumPad(
        val lotNumber: String
    ) : LotInteractionIntent()

    data class ToggleDelete(
        val lotNumber: String,
        val onHand: Int,
        val isCurrentlyDeleted: Boolean
    ) : LotInteractionIntent()

    /**
     * The scanner detected and parsed a barcode on the camera preview view
     *
     * @property barcode barcode parsed - should be TwoDeeBarcode if valid scan
     */
    data class ScanLot(
        val barcode: ParsedBarcode
    ) : LotInteractionIntent()

    data object HighlightComplete : LotInteractionIntent()
}

@Immutable
data class LotInteractionState(
    val stockType: StockUi = StockUi.PRIVATE,
    val isScannerActive: Boolean = false,
    val lots: List<LotInventoryUi> = listOf(),
    val lotCountTotal: String = "",
    val lotCountOriginal: String? = null,
    val isActionRequired: Boolean = false,
    val error: LotInteractionError? = null,
    val searchedLot: String? = null,
    val antigen: String = "",
    val prettyName: String = "",
    val cartonCount: Int = 0,
    val presentation: Presentation = Presentation.UNKNOWN,
    override val activeDialog: DialogKey? = null,
) : UiState,
    ActiveDialog

sealed class LotInteractionDialog : DialogKey {
    data class NumPadEntry(val lotName: String) : LotInteractionDialog()

    data class WrongProductScanned(val errorMessage: AnnotatedString) : LotInteractionDialog()

    data object SaveOrDiscardChanges : LotInteractionDialog()

    data object ExpiredProductScanned : LotInteractionDialog()

    data object MismatchedProduct : LotInteractionDialog()
}

sealed class LotInteractionEvent : UiEvent {
    data object NavigateBack : LotInteractionEvent()

    data class NavigateToLotSearch(
        val filterProductId: Int?,
        val sourceId: Int
    ) : LotInteractionEvent()
}

sealed class LotInteractionError {
    data object ProductNotFound : LotInteractionError()

    data object BadBarcodeScan : LotInteractionError()
}
