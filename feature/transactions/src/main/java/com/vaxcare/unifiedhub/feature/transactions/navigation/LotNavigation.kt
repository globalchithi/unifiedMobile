package com.vaxcare.unifiedhub.feature.transactions.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import kotlinx.serialization.Serializable

@Serializable
data class LotSearchRoute(
    val filterProductId: Int? = null,
    val sourceId: Int,
    val transactionName: String,
    val addNewLotEnabled: Boolean = true,
    val filterExpiredLots: Boolean = false
)

fun NavController.navigateToLotSearch(
    sourceId: Int,
    transactionName: String,
    filterProductId: Int? = null,
    addNewLotEnabled: Boolean = true,
    filterExpiredLots: Boolean = false,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) = navigate(
    route = LotSearchRoute(
        filterProductId = filterProductId,
        addNewLotEnabled = addNewLotEnabled,
        filterExpiredLots = filterExpiredLots,
        sourceId = sourceId,
        transactionName = transactionName
    ),
    builder = navOptions
)

@Serializable
data class AddLotRoute(
    val lotNumber: String,
    val productId: Int? = null
)

fun NavController.navigateToAddLot(
    lotNumber: String,
    productId: Int?,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) = navigate(
    route = AddLotRoute(lotNumber, productId),
    builder = navOptions
)
