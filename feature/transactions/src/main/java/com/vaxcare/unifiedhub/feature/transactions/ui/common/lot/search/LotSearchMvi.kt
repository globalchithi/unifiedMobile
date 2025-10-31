package com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search

import androidx.compose.ui.text.input.TextFieldValue
import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState

sealed class LotSearchIntent : UiIntent {
    data object CloseScreen : LotSearchIntent()

    /**
     * User tapped on "Cancel" from the Add Lot Dialog
     */
    data object CancelAddNewLot : LotSearchIntent()

    /**
     * User tapped on "Add New Lot" button
     *
     * @property enteredLotNumber Lot number to pass to Add New Lot Dialog
     */
    data class AddNewLot(
        val enteredLotNumber: String
    ) : LotSearchIntent()

    /**
     * User tapped on "Confirm" from the Add Lot Dialog
     *
     * @property enteredLotNumber Lot number to pass to Add New Lot flow
     */
    data class ConfirmedAddNewLot(
        val enteredLotNumber: String
    ) : LotSearchIntent()

    /**
     * Text in the Lot Search Input Field changed
     *
     * @property enteredLotNumber changed search term - must be > 3 and < 11 characters
     */
    data class SearchLot(
        val enteredLotNumber: TextFieldValue
    ) : LotSearchIntent()

    /**
     * User tapped a lot item
     *
     * @property selectedLot item to convert to LotNumberWithProduct and passed back to the
     * caller of the Lot Search flow
     */
    data class SelectLot(
        val selectedLot: SelectedLot
    ) : LotSearchIntent()
}

data class LotSearchState(
    /**
     * Items to show on the ui
     */
    val selectedLots: List<SelectedLot> = emptyList(),
    /**
     * Search key seen in the ui user is entering
     */
    val searchTerm: TextFieldValue = TextFieldValue(),
    val addNewLotEnabled: Boolean = true,
    override val activeDialog: DialogKey? = null
) : UiState,
    ActiveDialog

sealed class LotSearchDialog : DialogKey {
    data class ConfirmLotNumber(
        val enteredLotNumber: String
    ) : LotSearchDialog()
}

sealed class LotSearchEvent : UiEvent {
    data object NavigateBack : LotSearchEvent()

    /**
     * Navigate to the caller with the result lotNumber and source
     */
    data class NavigateWithSelectedLot(
        val selectedLotNumber: String,
        val sourceId: Int
    ) : LotSearchEvent()

    data class NavigateToAddLot(
        val lotNumber: String,
        val productId: Int? = null
    ) : LotSearchEvent()
}
