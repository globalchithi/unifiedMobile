package com.vaxcare.unifiedhub.feature.home.homeroute

import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.feature.home.ui.home.model.TransactionType
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.HomeRouteDialog
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.HomeRouteEvent
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.HomeRouteIntent
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.HomeRouteState
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.ManageMenuItem

data class EventScenario(val intent: HomeRouteIntent, val event: HomeRouteEvent)

object StockScenarios {
    val onlyPrivateStock = listOf(StockType.PRIVATE)

    val multipleStocks = listOf(
        StockType.PRIVATE,
        StockType.VFC,
        StockType.STATE,
        StockType.THREE_SEVENTEEN
    )

    val onlyOnePublicStock = listOf(StockType.PRIVATE, StockType.VFC)
}

object ExpectedMenuItems {
    val privateWithTransfers = listOf(
        ManageMenuItem.COUNT,
        ManageMenuItem.TRANSFER,
        ManageMenuItem.LOG_WASTE,
        ManageMenuItem.RETURNS,
        ManageMenuItem.BUYBACK
    )

    val privateNoTransfers = listOf(
        ManageMenuItem.COUNT,
        ManageMenuItem.LOG_WASTE,
        ManageMenuItem.RETURNS,
        ManageMenuItem.BUYBACK
    )

    val publicWithTransfers = listOf(
        ManageMenuItem.COUNT,
        ManageMenuItem.TRANSFER,
        ManageMenuItem.LOG_WASTE,
        ManageMenuItem.RETURNS,
        ManageMenuItem.ADD_DOSES
    )

    val publicNoTransfers = listOf(
        ManageMenuItem.COUNT,
        ManageMenuItem.TRANSFER,
        ManageMenuItem.LOG_WASTE,
        ManageMenuItem.RETURNS,
        ManageMenuItem.ADD_DOSES
    )
}

object TransactionNavigationTestScenarios {
    val onlyPrivateStock = listOf(
        EventScenario(
            intent = HomeRouteIntent.GoToCount,
            event = HomeRouteEvent.NavigateToTransaction(
                TransactionType.COUNTS,
                StockType.PRIVATE,
                shouldConfirmStock = false
            )
        ),
        EventScenario(
            intent = HomeRouteIntent.GoToLogWaste,
            event = HomeRouteEvent.NavigateToTransaction(
                TransactionType.LOG_WASTE,
                StockType.PRIVATE,
                shouldConfirmStock = false
            )
        ),
        EventScenario(
            intent = HomeRouteIntent.GoToReturns,
            event = HomeRouteEvent.NavigateToTransaction(
                TransactionType.RETURNS,
                StockType.PRIVATE,
                shouldConfirmStock = false
            )
        ),
    )

    val multipleStocks = listOf(
        EventScenario(
            intent = HomeRouteIntent.GoToCount,
            event = HomeRouteEvent.NavigateToTransaction(
                TransactionType.COUNTS,
                StockType.PRIVATE,
                shouldConfirmStock = true
            )
        ),
        EventScenario(
            intent = HomeRouteIntent.GoToLogWaste,
            event = HomeRouteEvent.NavigateToTransaction(
                TransactionType.LOG_WASTE,
                StockType.PRIVATE,
                shouldConfirmStock = true
            )
        ),
        EventScenario(
            intent = HomeRouteIntent.GoToReturns,
            event = HomeRouteEvent.NavigateToTransaction(
                TransactionType.RETURNS,
                StockType.PRIVATE,
                shouldConfirmStock = true
            )
        ),
    )

    val onlyOnePublicStock = HomeRouteEvent.NavigateToTransaction(
        TransactionType.ADD_PUBLIC,
        StockType.VFC,
        shouldConfirmStock = false
    )

    val multiplePublicStock = HomeRouteEvent.NavigateToTransaction(
        TransactionType.ADD_PUBLIC,
        StockType.VFC,
        shouldConfirmStock = true
    )
}

object NoInternetScenarios {
    object NoInternetDialog {
        val expectedUiState = HomeRouteState(
            activeDialog = HomeRouteDialog.NoInternet(allowRetry = true),
            manageMenuItems = ExpectedMenuItems.privateWithTransfers
        )
    }

    object NetworkSettingsDialog {
        val expectedUiState = HomeRouteState(
            activeDialog = HomeRouteDialog.NoInternet(allowRetry = false),
            manageMenuItems = ExpectedMenuItems.privateWithTransfers
        )
    }
}
