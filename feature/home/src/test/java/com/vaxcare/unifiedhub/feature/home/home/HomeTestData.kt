package com.vaxcare.unifiedhub.feature.home.home

import com.vaxcare.unifiedhub.core.model.Location
import com.vaxcare.unifiedhub.core.model.inventory.LotInventory
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeDialog
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeState
import com.vaxcare.unifiedhub.feature.home.ui.home.model.AdjustmentListItemUi
import com.vaxcare.unifiedhub.feature.home.ui.home.model.Notification

object HomeTestData {
    object AvailableStocksChanged {
        val newLocation = Location(
            clinicName = "New",
            partnerName = "New",
            stockTypes = listOf(
                StockType.PRIVATE,
                StockType.VFC,
                StockType.THREE_SEVENTEEN
            )
        )

        val expectedUiState = HomeState(
            clinicName = "New",
            partnerName = "New",
            activeStock = StockUi.PRIVATE,
            availableStocks = listOf(
                StockUi.PRIVATE,
                StockUi.VFC,
                StockUi.THREE_SEVENTEEN,
            )
        )
    }

    object OnlyCountsOverdue {
        val expectedUiState = HomeState(
            notifications = setOf(
                Notification.OverdueCount(17)
            )
        )
    }

    object OnlyExpiredDoses {
        val expiredInventory = listOf(
            LotInventory(
                lotNumber = "",
                onHand = 5,
                inventorySourceId = 1,
            ),
            LotInventory(
                lotNumber = "",
                onHand = 8,
                inventorySourceId = 1,
            )
        )
        val expectedUiState = HomeState(
            notifications = setOf(
                Notification.ExpiredDoses(13)
            )
        )
    }

    object OnlyAppUpdate {
        val expectedUiState = HomeState(
            notifications = setOf(Notification.AppUpdate)
        )
    }

    object EmptyNotifications {
        val expectedUiState = HomeState()
    }

    object NoInternetDialog {
        val expectedUiState = HomeState().copy(
            activeDialog = HomeDialog.NoInternet(allowRetry = true)
        )
    }

    object NetworkSettingsDialog {
        val expectedUiState = HomeState().copy(
            activeDialog = HomeDialog.NoInternet(allowRetry = false)
        )
    }

    object ShowTransfers {
        val expectedUiState = HomeState(
            activeDialog = HomeDialog.AdjustInventory(
                listOf(
                    AdjustmentListItemUi.Returns,
                    AdjustmentListItemUi.LogWaste,
                    AdjustmentListItemUi.Transfer,
                    AdjustmentListItemUi.Buyback,
                )
            )
        )
    }

    object HideTransfers {
        val expectedUiState = HomeState(
            activeDialog = HomeDialog.AdjustInventory(
                listOf(
                    AdjustmentListItemUi.Returns,
                    AdjustmentListItemUi.LogWaste,
                    AdjustmentListItemUi.Buyback,
                )
            )
        )
    }
}
