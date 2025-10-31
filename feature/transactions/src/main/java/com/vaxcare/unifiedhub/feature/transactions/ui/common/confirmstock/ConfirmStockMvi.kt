package com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock

import androidx.annotation.StringRes
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi

sealed class ConfirmStockIntent : UiIntent {
    data object Close : ConfirmStockIntent()

    data class StockSelected(val stock: StockUi) : ConfirmStockIntent()

    data object ConfirmStock : ConfirmStockIntent()
}

sealed class ConfirmStockEvent : UiEvent {
    data class StockConfirmed(val stock: StockType) : ConfirmStockEvent()

    data object GoBack : ConfirmStockEvent()
}

data class ConfirmStockState(
    @StringRes val title: Int? = null,
    @StringRes val subtitle: Int? = null,
    val selectedStock: StockUi = StockUi.PRIVATE,
    val stocks: List<StockUi> = emptyList()
) : UiState
