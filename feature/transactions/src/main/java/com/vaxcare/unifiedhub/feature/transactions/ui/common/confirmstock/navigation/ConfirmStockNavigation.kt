package com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock.navigation

import androidx.annotation.StringRes
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmStockRoute(
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    val preselectedStock: StockType,
    val publicStocksOnly: Boolean
)

fun NavController.navigateToConfirmStock(
    @StringRes title: Int,
    @StringRes subtitle: Int,
    preselectedStock: StockType,
    publicStocksOnly: Boolean = false,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) = navigate(
    route = ConfirmStockRoute(title, subtitle, preselectedStock, publicStocksOnly),
    builder = navOptions
)
