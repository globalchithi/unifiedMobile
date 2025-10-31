package com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete

import androidx.annotation.StringRes
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi

sealed interface CountsCompleteIntent : UiIntent {
    data object LogOut : CountsCompleteIntent

    data object BackToHome : CountsCompleteIntent
}

sealed interface CountsCompleteEvent : UiEvent {
    data object NavigateToHome : CountsCompleteEvent
}

data class CountsCompleteState(
    val stockType: StockUi = StockUi.PRIVATE,
    val date: String = "",
    val showImpact: Boolean = false,
    val showVariance: Boolean = false,
    @StringRes val disclaimerRes: Int? = null,
    val totalProducts: Int? = null,
    val totalUnits: Int? = null,
    val addedUnits: Int? = null,
    val addedImpact: String? = null,
    val missingUnits: Int? = null,
    val missingImpact: String? = null,
    val totalImpact: String? = null,
    val inventoryBalance: String = "",
) : UiState
