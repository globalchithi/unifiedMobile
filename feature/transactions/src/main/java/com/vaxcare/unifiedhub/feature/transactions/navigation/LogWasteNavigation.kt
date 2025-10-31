package com.vaxcare.unifiedhub.feature.transactions.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import kotlinx.serialization.Serializable

@Serializable
data class LogWasteSectionRoute(
    val stockType: StockType,
    val shouldConfirmStock: Boolean
)

fun NavGraphBuilder.logWasteSection(startDestination: Any, builder: NavGraphBuilder.() -> Unit) {
    navigation<LogWasteSectionRoute>(startDestination = startDestination, builder = builder)
}

fun NavController.navigateToLogWasteSection(
    stockType: StockType,
    shouldConfirmStock: Boolean,
    navOptions: NavOptions = navOptions {}
) {
    navigate(route = LogWasteSectionRoute(stockType, shouldConfirmStock), navOptions)
}

@Serializable
data object LogWasteReasonRoute

fun NavController.navigateToLogWasteReason(navOptions: NavOptions = navOptions {}) {
    navigate(route = LogWasteReasonRoute, navOptions)
}

@Serializable
data object LogWasteRoute

fun NavController.navigateToLogWaste(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route = LogWasteRoute, navOptions)
}

@Serializable
data object LogWasteSummaryRoute

fun NavController.navigateToLogWasteSummary(navOptions: NavOptions = navOptions { }) {
    navigate(route = LogWasteSummaryRoute, navOptions)
}

@Serializable
data object LogWasteCompleteRoute

fun NavController.navigateToLogWasteComplete(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route = LogWasteCompleteRoute, navOptions)
}
