package com.vaxcare.unifiedhub.feature.home.ui.onhand

import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.home.ui.onhand.model.GroupedProducts
import com.vaxcare.unifiedhub.feature.home.ui.onhand.model.OnHandSection

sealed interface OnHandDialog : DialogKey {
    data object StockSelection : OnHandDialog
}

sealed interface OnHandIntent : UiIntent {
    data object OpenStockSelector : OnHandIntent

    data class SelectStock(val stock: StockUi) : OnHandIntent

    data class SelectSection(
        val section: OnHandSection
    ) : OnHandIntent

    data object DismissDialog : OnHandIntent
}

sealed interface OnHandEvent : UiEvent {
    data class ScrollToItem(
        val index: Int
    ) : OnHandEvent
}

data class OnHandState(
    override val activeDialog: DialogKey? = null,
    val availableStocks: List<StockUi> = listOf(),
    val enableStockSelection: Boolean = false,
    val activeStock: StockUi = StockUi.PRIVATE,
    val seasonalProducts: List<GroupedProducts> = listOf(),
    val nonSeasonalProducts: List<GroupedProducts> = listOf(),
) : UiState,
    ActiveDialog
