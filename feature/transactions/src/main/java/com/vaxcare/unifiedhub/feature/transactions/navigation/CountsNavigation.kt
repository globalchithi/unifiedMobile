package com.vaxcare.unifiedhub.feature.transactions.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.model.CountTotals
import kotlinx.serialization.Serializable

@Serializable
data class CountsSectionRoute(
    val stockType: StockType,
    val shouldConfirmStock: Boolean
)

@Serializable
data object CountsHomeRoute

@Serializable
data object CountsSubmitRoute

@Serializable
data class CountsCompleteRoute(
    val stockType: StockType,
    val products: Int? = null,
    val units: Int? = null,
    val addedUnits: Int? = null,
    val addedImpact: Float? = null,
    val missingUnits: Int? = null,
    val missingImpact: Float? = null,
)

fun NavController.navigateToCounts(
    stockType: StockType,
    shouldConfirmStock: Boolean,
    navOptions: NavOptions = navOptions {
    }
) {
    navigate(route = CountsSectionRoute(stockType, shouldConfirmStock), navOptions)
}

fun NavController.navigateToCountsHome(navOptions: NavOptions = navOptions {}) {
    navigate(route = CountsHomeRoute, navOptions)
}

fun NavController.navigateToCountsSubmit(navOptions: NavOptions = navOptions {}) {
    navigate(route = CountsSubmitRoute, navOptions)
}

fun NavController.navigateToCountsComplete(
    totals: CountTotals,
    stockType: StockType,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = CountsCompleteRoute(
            stockType = stockType,
            products = totals.products,
            units = totals.units,
            addedUnits = totals.addedUnits,
            addedImpact = totals.addedImpact,
            missingUnits = totals.missingUnits,
            missingImpact = totals.missingImpact
        ),
        builder = builder
    )
}

fun NavGraphBuilder.countsSection(startDestination: Any, builder: NavGraphBuilder.() -> Unit) {
    navigation<CountsSectionRoute>(startDestination = startDestination, builder = builder)
}

@Serializable
data object LotInteractionRoute

fun NavController.navigateToLotInteraction(navOptions: NavOptionsBuilder.() -> Unit = {}) =
    navigate(route = LotInteractionRoute, builder = navOptions)
