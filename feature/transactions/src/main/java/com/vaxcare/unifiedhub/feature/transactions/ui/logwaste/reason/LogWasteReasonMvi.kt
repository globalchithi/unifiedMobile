package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason

import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason

sealed class LogWasteReasonIntent : UiIntent {
    data class SelectReason(val reason: LogWasteReason) : LogWasteReasonIntent()

    data object ConfirmReason : LogWasteReasonIntent()

    data object CloseDialog : LogWasteReasonIntent()

    data object GoBack : LogWasteReasonIntent()
}

sealed class LogWasteReasonEvent : UiEvent {
    data class ReasonConfirmed(val reason: LogWasteReason) : LogWasteReasonEvent()

    data object NavigateBack : LogWasteReasonEvent()
}

sealed class LogWasteReasonDialog : DialogKey {
    data object ReturnExpiredProducts : LogWasteReasonDialog()

    data object ReturnProductsDeliveredOutOfTemp : LogWasteReasonDialog()
}

data class LogWasteReasonState(
    val selectedReason: LogWasteReason? = null,
    val stock: StockUi = StockUi.PRIVATE,
    override val activeDialog: LogWasteReasonDialog? = null
) : UiState, ActiveDialog
