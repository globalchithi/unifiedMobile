package com.vaxcare.unifiedhub.feature.transactions.ui.returns.complete

import com.vaxcare.unifiedhub.core.model.inventory.ReturnReason
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductUi

object ReturnsCompleteSampleData {
    val Default = ReturnsCompleteState(
        products = ProductUi.Sample,
        date = "October 02, 2025",
        shipmentPickup = "Mon, 10/06 9AM - 5PM",
        totalProducts = "78",
        stockType = StockUi.PRIVATE,
        reason = ReturnReason.EXPIRED
    )
}
