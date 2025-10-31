package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary

import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUi

sealed class AddPublicSummaryIntent : UiIntent {
    data object SubmitAddPublic : AddPublicSummaryIntent()

    data object RetrySubmission : AddPublicSummaryIntent()

    data object DismissDialog : AddPublicSummaryIntent()

    data object GoBack : AddPublicSummaryIntent()
}

sealed class AddPublicSummaryEvent : UiEvent {
    data object NavigateBack : AddPublicSummaryEvent()

    data object NavigateToAddPublicCompleted : AddPublicSummaryEvent()
}

sealed class AddPublicSummaryDialog : DialogKey {
    data object SubmissionFailed : AddPublicSummaryDialog()
}

data class AddPublicSummaryState(
    val stock: StockUi = StockUi.PRIVATE,
    val products: List<ProductUi> = emptyList(),
    val total: Int = 0,
    val isLoading: Boolean = false,
    override val activeDialog: DialogKey? = null
) : UiState, ActiveDialog
