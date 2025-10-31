package com.vaxcare.unifiedhub.core.domain

import com.vaxcare.unifiedhub.core.model.inventory.StockType
import javax.inject.Inject

class ValidateStockType @Inject constructor() {
    operator fun invoke(stockType: StockType, availableStocks: List<StockType>) =
        if (availableStocks.contains(stockType)) {
            stockType
        } else {
            StockType.PRIVATE
        }
}
