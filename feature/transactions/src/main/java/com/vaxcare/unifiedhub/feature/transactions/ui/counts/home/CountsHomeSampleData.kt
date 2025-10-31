package com.vaxcare.unifiedhub.feature.transactions.ui.counts.home

import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.model.ProductUi

object CountsHomeSampleData {
    val Default = CountsHomeState(
        isLoading = false,
        stockType = StockUi.PRIVATE,
        nonSeasonalProducts = ProductUi.Sample.take(4),
        seasonalProducts = ProductUi.Sample.takeLast(4),
    )
    val NoActionRequired = CountsHomeState(
        isLoading = false,
        stockType = StockUi.PRIVATE,
        nonSeasonalProducts = Default.nonSeasonalProducts.map {
            it.copy(
                inventory = it.inventory.map { inventory ->
                    inventory.copy(initialQuantity = 0)
                }
            )
        },
        seasonalProducts = Default.seasonalProducts.map {
            it.copy(
                inventory = it.inventory.map { inventory ->
                    inventory.copy(initialQuantity = 0)
                }
            )
        },
    )
}
