package com.vaxcare.unifiedhub.feature.home.ui.home

import androidx.compose.runtime.Immutable
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.home.ui.home.model.AdjustmentListItemUi
import com.vaxcare.unifiedhub.feature.home.ui.home.model.Notification

sealed interface HomeDialog : DialogKey {
    data object StockSelection : HomeDialog

    data class AdjustInventory(
        val adjustmentItems: List<AdjustmentListItemUi>
    ) : HomeDialog

    data class NoInternet(
        val allowRetry: Boolean = true
    ) : HomeDialog
}

sealed interface HomeIntent : UiIntent {
    data object OpenStockSelector : HomeIntent

    data object AdjustInventory : HomeIntent

    data class SelectStock(val stock: StockUi) : HomeIntent

    data object ShowHamburgerMenu : HomeIntent

    data class SelectAdjustment(val selectedAdjustment: AdjustmentListItemUi) : HomeIntent

    data object DismissDialog : HomeIntent

    data object ApplyAppUpdate : HomeIntent

    data object ReturnExpiredDoses : HomeIntent

    data object GoToCount : HomeIntent

    data object GoToLogWaste : HomeIntent

    data object GoToOnHand : HomeIntent

    data object NoInternetTryAgain : HomeIntent

    data object GoToNetworkSettings : HomeIntent
}

sealed interface HomeEvent : UiEvent {
    data class NavigateToCount(
        val stockType: StockType,
        val shouldConfirmStock: Boolean,
    ) : HomeEvent

    data class NavigateToAddPublic(
        val stockType: StockType,
        val shouldConfirmStock: Boolean,
    ) : HomeEvent

    data class NavigateToBuyback(
        val stockType: StockType
    ) : HomeEvent

    data class NavigateToLogWaste(
        val stockType: StockType,
        val shouldConfirmStock: Boolean,
    ) : HomeEvent

    data class NavigateToReturns(
        val stockType: StockType,
        val shouldConfirmStock: Boolean = true,
        val preLoadExpired: Boolean = false,
    ) : HomeEvent

    data class NavigateToTransfer(
        val stockType: StockType
    ) : HomeEvent

    data object NavigateToAdmin : HomeEvent

    data object LaunchAppUpdate : HomeEvent

    data object OpenHamburgerMenu : HomeEvent

    data object LaunchNetworkSettings : HomeEvent
}

@Immutable
data class HomeState(
    override val activeDialog: DialogKey? = null,
    val availableStocks: List<StockUi> = listOf(),
    val activeStock: StockUi = StockUi.PRIVATE,
    val clinicName: String? = null,
    val partnerName: String? = null,
    val notifications: Set<Notification> = setOf(),
) : UiState,
    ActiveDialog
