package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.AnnotatedString
import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.model.ProductUi
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode

sealed interface AddPublicHomeIntent : UiIntent {
    data class EditProduct(
        val product: ProductUi
    ) : AddPublicHomeIntent

    data class DeleteProduct(
        val product: ProductUi
    ) : AddPublicHomeIntent

    data class RestoreProduct(
        val product: ProductUi
    ) : AddPublicHomeIntent

    data object CloseScreen : AddPublicHomeIntent

    data object DiscardChanges : AddPublicHomeIntent

    data object SearchLots : AddPublicHomeIntent

    data object ProceedToSummary : AddPublicHomeIntent

    data object DismissDialog : AddPublicHomeIntent

    /**
     * The scanner detected and parsed a barcode on the camera preview view
     *
     * @property barcode barcode parsed - should be TwoDeeBarcode if valid scan
     */
    data class ScanLot(
        val barcode: ParsedBarcode
    ) : AddPublicHomeIntent
}

sealed interface AddPublicHomeEvent : UiEvent {
    data object NavigateBack : AddPublicHomeEvent

    data object NavigateToSummary : AddPublicHomeEvent

    data object NavigateToLotInteraction : AddPublicHomeEvent

    data class NavigateToLotSearch(
        val sourceId: Int
    ) : AddPublicHomeEvent

    data class ScrollToItem(
        val index: Int
    ) : AddPublicHomeEvent
}

sealed interface AddPublicHomeDialog : DialogKey {
    data class WrongProductScanned(val errorMessage: AnnotatedString) : AddPublicHomeDialog

    data object ExpiredProductScanned : AddPublicHomeDialog

    data object DiscardChanges : AddPublicHomeDialog
}

data class AddPublicHomeState(
    val isScannerActive: Boolean = false,
    val products: List<ProductUi> = mutableStateListOf(),
    val stockType: StockUi = StockUi.PRIVATE,
    val isInvalidScan: Boolean = false,
    override val activeDialog: DialogKey? = null,
) : UiState, ActiveDialog {
    val total: Int
        get() = products
            .filterNot { it.isDeleted }
            .sumOf { it.getQuantity() }
}
