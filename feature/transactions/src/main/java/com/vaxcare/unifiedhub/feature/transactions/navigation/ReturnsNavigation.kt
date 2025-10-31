package com.vaxcare.unifiedhub.feature.transactions.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.vaxcare.unifiedhub.core.model.inventory.ReturnReason
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import kotlinx.serialization.Serializable

@Serializable
data class ReturnsSectionRoute(
    val stockType: StockType,
    val reason: ReturnReason? = null,
    val skipStockConfirmation: Boolean = false,
    val skipReasonSelection: Boolean = false,
)

@Serializable
data class ReturnReasonRoute(val stockType: StockType)

@Serializable
data class ReturnProductInteractionRoute(
    val stockType: StockType,
    val reason: ReturnReason,
)

@Serializable
data class ReturnsWindowRoute(
    val stockType: StockType,
    val reason: ReturnReason,
)

@Serializable
data class ReturnsSummaryRoute(
    val stockType: StockType,
    val reason: ReturnReason,
    val noOfLabels: Int? = null,
)

@Serializable
data class ReturnsCompleteRoute(
    val stockType: StockType,
    val reason: ReturnReason
)

fun NavController.navigateToReturns(
    stockType: StockType,
    reason: ReturnReason? = null,
    skipStockConfirmation: Boolean = false,
    skipReasonSelection: Boolean = false,
    navOptions: NavOptions = navOptions {}
) {
    navigate(route = ReturnsSectionRoute(stockType, reason, skipStockConfirmation, skipReasonSelection), navOptions)
}

fun NavController.navigateToReturnsReason(stockType: StockType, navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route = ReturnReasonRoute(stockType), navOptions)
}

fun NavController.navigateToReturnsProductInteraction(
    stockType: StockType,
    reason: ReturnReason,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(
        route = ReturnProductInteractionRoute(stockType, reason),
        builder = navOptions
    )
}

fun NavController.navigateToReturnsWindow(
    stockType: StockType,
    reason: ReturnReason,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(
        route = ReturnsWindowRoute(stockType, reason),
        builder = navOptions
    )
}

/**
 * @param noOfLabels Used when navigating from [ReturnsWindowRoute].
 */
fun NavController.navigateToReturnsSummary(
    stockType: StockType,
    reason: ReturnReason,
    noOfLabels: Int? = null,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(
        route = ReturnsSummaryRoute(stockType, reason, noOfLabels),
        builder = navOptions
    )
}

fun NavController.navigateToReturnsComplete(
    stockType: StockType,
    reason: ReturnReason,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route = ReturnsCompleteRoute(stockType, reason), builder = navOptions)
}

fun NavGraphBuilder.returnsSection(startDestination: Any, builder: NavGraphBuilder.() -> Unit) {
    navigation<ReturnsSectionRoute>(startDestination = startDestination, builder = builder)
}
