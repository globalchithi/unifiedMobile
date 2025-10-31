package com.vaxcare.unifiedhub.feature.transactions.ui.returns.window

import com.vaxcare.unifiedhub.core.model.PickupAvailability
import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import kotlin.collections.isNotEmpty

sealed class ReturnsWindowIntent : UiIntent {
    object NavigateBack : ReturnsWindowIntent()

    data class SelectPickUp(val index: Int) : ReturnsWindowIntent()

    data object IncrementLabelQuantity : ReturnsWindowIntent()

    data object DecrementLabelQuantity : ReturnsWindowIntent()

    data object EditLabelQuantity : ReturnsWindowIntent()

    data class SetLabelQuantity(val quantity: Int) : ReturnsWindowIntent()

    object CloseDialog : ReturnsWindowIntent()

    object GetNextAvailablePickUps : ReturnsWindowIntent()

    object GetPreviousAvailablePickUps : ReturnsWindowIntent()

    sealed class NoInternetConnectionIntent : ReturnsWindowIntent() {
        object TryAgain : ReturnsWindowIntent()

        object Cancel : ReturnsWindowIntent()
    }

    object Confirm : ReturnsWindowIntent()
}

sealed class ReturnsWindowEvent : UiEvent {
    object NavigateBack : ReturnsWindowEvent()

    data class NavigateToSummary(
        val noOfLabels: Int?,
    ) : ReturnsWindowEvent()
}

sealed class ReturnsWindowDialog : DialogKey {
    object EditShippingLabelQuantity : ReturnsWindowDialog()

    object Retry : ReturnsWindowDialog()
}

data class ReturnsWindowState(
    val loading: Boolean = true,
    val loadingNextAvailablePickUps: Boolean = false,
    val availablePickups: List<PickupAvailability> = emptyList(),
    val pageIndex: Int = 0,
    val selectedIndex: Int? = null,
    val shippingLabels: Int = 1,
    override val activeDialog: DialogKey? = null
) : UiState, ActiveDialog {
    companion object {
        const val MAX_SHIPPING_LABEL_QUANTITY = 10
        const val MIN_SHIPPING_LABEL_QUANTITY = 1

        const val PAGE_SIZE = 3
    }

    val canConfirm: Boolean
        get() = availablePickups.any { selectedIndex != null && shippingLabels > 0 }

    val canIncrementShippingLabels: Boolean
        get() = shippingLabels < MAX_SHIPPING_LABEL_QUANTITY

    val canDecrementShippingLabels: Boolean
        get() = shippingLabels > MIN_SHIPPING_LABEL_QUANTITY

    val isPaginationEnabled: Boolean
        get() = availablePickups.isNotEmpty() && !loadingNextAvailablePickUps
}
