package com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason

import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi

sealed class ReturnReasonIntent : UiIntent {
    data class SelectReason(val reason: ReturnReasonUi) : ReturnReasonIntent()

    data object ConfirmReason : ReturnReasonIntent()

    data object GoBack : ReturnReasonIntent()
}

sealed class ReturnReasonEvent : UiEvent {
    data class ReasonConfirmed(val reason: ReturnReasonUi) : ReturnReasonEvent()

    data object NavigateBack : ReturnReasonEvent()
}

data class ReturnReasonState(
    val selectedReason: ReturnReasonUi? = null,
    val stock: StockUi = StockUi.PRIVATE
) : UiState
