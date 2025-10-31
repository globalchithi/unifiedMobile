package com.vaxcare.unifiedhub.feature.transactions.ui.counts.home

import androidx.compose.runtime.mutableStateListOf
import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.model.CountsSection
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.model.ProductUi

sealed interface CountsHomeIntent : UiIntent {
    data class EditProduct(
        val product: ProductUi
    ) : CountsHomeIntent

    data class ConfirmProduct(
        val product: ProductUi
    ) : CountsHomeIntent

    data object CloseScreen : CountsHomeIntent

    data object DiscardChanges : CountsHomeIntent

    data object SearchLots : CountsHomeIntent

    data object ProceedToSummary : CountsHomeIntent

    data object DismissDialog : CountsHomeIntent

    data class SelectSection(
        val section: CountsSection
    ) : CountsHomeIntent

    data object NoInternetTryAgain : CountsHomeIntent
}

sealed interface CountsHomeEvent : UiEvent {
    data object NavigateBack : CountsHomeEvent

    data object NavigateToSummary : CountsHomeEvent

    data object NavigateToLotInteraction : CountsHomeEvent

    data class NavigateToLotSearch(
        val sourceId: Int
    ) : CountsHomeEvent

    data class ScrollToItem(
        val index: Int
    ) : CountsHomeEvent
}

sealed interface CountsHomeDialog : DialogKey {
    data object DiscardChanges : CountsHomeDialog

    data object ActionRequired : CountsHomeDialog

    data object NoInternet : CountsHomeDialog
}

data class CountsHomeState(
    val isLoading: Boolean = true,
    val seasonalProducts: List<ProductUi> = mutableStateListOf(),
    val nonSeasonalProducts: List<ProductUi> = mutableStateListOf(),
    val stockType: StockUi = StockUi.PRIVATE,
    override val activeDialog: DialogKey? = null,
) : UiState,
    ActiveDialog
