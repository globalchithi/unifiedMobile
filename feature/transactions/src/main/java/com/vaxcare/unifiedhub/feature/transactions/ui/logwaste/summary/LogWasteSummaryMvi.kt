package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary

import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.model.ProductUi

sealed class LogWasteSummaryIntent : UiIntent {
    data object SubmitLogWaste : LogWasteSummaryIntent()

    data object RetrySubmission : LogWasteSummaryIntent()

    data object DismissDialog : LogWasteSummaryIntent()

    data object GoBack : LogWasteSummaryIntent()
}

sealed class LogWasteSummaryEvent : UiEvent {
    data object NavigateBack : LogWasteSummaryEvent()

    data object NavigateToLogWasteCompleted : LogWasteSummaryEvent()
}

sealed class LogWasteSummaryDialog : DialogKey {
    data object SubmissionFailed : LogWasteSummaryDialog()
}

data class LogWasteSummaryState(
    val reason: LogWasteReason = LogWasteReason.DELIVER_OUT_OF_TEMP,
    val stock: StockUi = StockUi.PRIVATE,
    val products: List<ProductUi> = emptyList(),
    val total: String = "",
    val isLoading: Boolean = false,
    override val activeDialog: DialogKey? = null
) : UiState, ActiveDialog
