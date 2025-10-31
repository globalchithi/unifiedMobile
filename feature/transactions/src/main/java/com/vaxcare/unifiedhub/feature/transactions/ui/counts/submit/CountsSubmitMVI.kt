package com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit

import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.model.CountTotals
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.model.ProductUi

sealed interface CountsSubmitDialog : DialogKey {
    data object SubmissionFailed : CountsSubmitDialog
}

sealed interface CountsSubmitIntent : UiIntent {
    data object GoBack : CountsSubmitIntent

    data object ConfirmCount : CountsSubmitIntent

    data object RetrySubmit : CountsSubmitIntent

    data object DismissDialog : CountsSubmitIntent
}

sealed interface CountsSubmitEvent : UiEvent {
    data object NavigateBack : CountsSubmitEvent

    data class NavigateToConfirmation(
        val totals: CountTotals
    ) : CountsSubmitEvent
}

data class CountsSubmitState(
    override val activeDialog: DialogKey? = null,
    val isLoading: Boolean = false,
    val stockType: StockUi = StockUi.PRIVATE,
    val nonSeasonalProducts: List<ProductUi> = listOf(),
    val seasonalProducts: List<ProductUi> = listOf(),
    val subTotal: String = "",
    val showImpact: Boolean = true,
) : UiState,
    ActiveDialog
