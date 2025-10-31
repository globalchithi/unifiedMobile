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
data class AddPublicSectionRoute(
    val stockType: StockType,
    val shouldConfirmStock: Boolean
)

@Serializable
data object AddPublicHomeRoute

@Serializable
data object AddPublicLotInteractionRoute

@Serializable
data object AddPublicSummaryRoute

@Serializable
data object AddPublicCompleteRoute

fun NavController.navigateToAddPublic(
    stockType: StockType,
    shouldConfirmStock: Boolean,
    navOptions: NavOptions = navOptions {}
) {
    navigate(route = AddPublicSectionRoute(stockType, shouldConfirmStock), navOptions)
}

fun NavController.navigateToAddPublicHome(navOptions: NavOptions = navOptions { }) {
    navigate(route = AddPublicHomeRoute, navOptions)
}

fun NavController.navigateToAddPublicLotInteraction(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route = AddPublicLotInteractionRoute, navOptions)
}

fun NavController.navigateToAddPublicSummary(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route = AddPublicSummaryRoute, navOptions)
}

fun NavController.navigateToAddPublicComplete(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route = AddPublicCompleteRoute, navOptions)
}

fun NavGraphBuilder.addPublicSection(startDestination: Any, builder: NavGraphBuilder.() -> Unit) {
    navigation<AddPublicSectionRoute>(startDestination = startDestination, builder = builder)
}
