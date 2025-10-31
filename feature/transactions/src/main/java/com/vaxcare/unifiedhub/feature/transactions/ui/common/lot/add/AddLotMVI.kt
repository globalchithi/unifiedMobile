package com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add

import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.LotForm
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.PresentationUI
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.ProductUI
import java.time.LocalDate

sealed interface AddLotIntent : UiIntent {
    data object Close : AddLotIntent

    data object CloseDialog : AddLotIntent

    data object CreateLot : AddLotIntent

    data object OpenAntigenPicker : AddLotIntent

    data class AntigenPicked(val antigen: String) : AddLotIntent

    data object OpenProductPicker : AddLotIntent

    data class ProductPicked(val product: ProductUI) : AddLotIntent

    data object OpenPresentationPicker : AddLotIntent

    data class PresentationPicked(val presentation: PresentationUI) : AddLotIntent

    data object OpenExpirationPicker : AddLotIntent

    data class ExpirationPicked(val expirationDate: LocalDate) : AddLotIntent
}

sealed interface AddLotEvent : UiEvent {
    data object Close : AddLotEvent

    data class ConfirmLot(val newLot: String) : AddLotEvent
}

sealed interface AddLotDialog : DialogKey {
    data class AntigenPicker(val options: List<String>) : AddLotDialog

    data class ProductPicker(val options: List<ProductUI>) : AddLotDialog

    data class PresentationPicker(val options: List<PresentationUI>) : AddLotDialog

    data class ExpirationPicker(val expirationDate: LocalDate) : AddLotDialog
}

data class AddLotState(
    val loading: Boolean = true,
    val form: LotForm = LotForm(),
    override val activeDialog: AddLotDialog? = null,
) : UiState, ActiveDialog {
    val isCheckEnabled: Boolean get() = form.isComplete
}
