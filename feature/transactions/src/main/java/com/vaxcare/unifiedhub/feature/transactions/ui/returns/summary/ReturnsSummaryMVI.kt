package com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary

import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductUi
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason.ReturnReasonUi

sealed interface ReturnsSummaryIntent : UiIntent {
    data object SubmitReturn : ReturnsSummaryIntent

    data object RetrySubmission : ReturnsSummaryIntent

    data object DismissDialog : ReturnsSummaryIntent

    data object GoBack : ReturnsSummaryIntent
}

sealed interface ReturnsSummaryEvent : UiEvent {
    data object NavigateBack : ReturnsSummaryEvent

    data object NavigateToReturnCompleted : ReturnsSummaryEvent
}

sealed interface ReturnsSummaryDialog : DialogKey {
    data object SubmissionFailed : ReturnsSummaryDialog
}

data class ReturnsSummaryState(
    val stock: StockUi = StockUi.PRIVATE,
    val reason: ReturnReasonUi? = null,
    val products: List<ProductUi> = emptyList(),
    val total: Int = 0,
    val isLoading: Boolean = false,
    override val activeDialog: DialogKey? = null
) : UiState,
    ActiveDialog
