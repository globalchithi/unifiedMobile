package com.vaxcare.unifiedhub.feature.home.ui.homeroute

import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.feature.home.ui.home.model.TransactionType

sealed interface HomeRouteIntent : UiIntent {
    data object GoToAdmin : HomeRouteIntent

    data object GoToAdminInfo : HomeRouteIntent

    data object GoToCount : HomeRouteIntent

    data object GoToAddPublic : HomeRouteIntent

    data object GoToLogWaste : HomeRouteIntent

    data object GoToReturns : HomeRouteIntent

    data class GoToOnHand(
        val jumpToSeasonal: Boolean = false,
        val jumpToNonSeasonal: Boolean = false,
    ) : HomeRouteIntent

    data object JumpCompleted : HomeRouteIntent

    data object DismissDialog : HomeRouteIntent

    data object NoInternetTryAgain : HomeRouteIntent

    data object GoToNetworkSettings : HomeRouteIntent
}

sealed interface HomeRouteEvent : UiEvent {
    data class NavigateToTransaction(
        val transactionType: TransactionType,
        val stockType: StockType,
        val shouldConfirmStock: Boolean,
    ) : HomeRouteEvent

    data object NavigateToAdmin : HomeRouteEvent

    data object NavigateToAdminInfo : HomeRouteEvent

    data object ScrollToOnHand : HomeRouteEvent

    data object LaunchNetworkSettings : HomeRouteEvent
}

enum class ManageMenuItem {
    COUNT,
    TRANSFER,
    LOG_WASTE,
    RETURNS,
    BUYBACK,
    ADD_DOSES
}

data class HomeRouteState(
    // TODO: get product category from prefs once we support other categories
    val activeProductCategory: String = "Vaccines",
    val activeStock: StockType = StockType.PRIVATE,
    val jumpToSeasonal: Boolean = false,
    val jumpToNonSeasonal: Boolean = false,
    val manageMenuItems: List<ManageMenuItem> = listOf(),
    override val activeDialog: HomeRouteDialog? = null
) : UiState, ActiveDialog

sealed interface HomeRouteDialog : DialogKey {
    data class NoInternet(
        val allowRetry: Boolean = true
    ) : HomeRouteDialog
}
