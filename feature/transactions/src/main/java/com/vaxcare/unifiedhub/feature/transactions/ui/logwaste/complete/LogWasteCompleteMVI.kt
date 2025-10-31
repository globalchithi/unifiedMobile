package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.complete

import androidx.compose.runtime.Immutable
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.model.ProductUi

sealed interface LogWasteCompleteIntent : UiIntent {
    data object LogOut : LogWasteCompleteIntent

    data object BackToHome : LogWasteCompleteIntent
}

sealed interface LogWasteCompleteEvent : UiEvent {
    data object NavigateToHome : LogWasteCompleteEvent
}

@Immutable
data class LogWasteCompleteState(
    val stockType: StockUi = StockUi.PRIVATE,
    val products: List<ProductUi> = emptyList(),
    val date: String = "",
    val showImpact: Boolean = false,
    val totalImpact: String = "",
    val totalProducts: String = "",
) : UiState
