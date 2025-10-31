package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.complete

import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.model.ProductUi

object LogWasteCompleteSampleData {
    val WithImpact = LogWasteCompleteState(
        stockType = StockUi.PRIVATE,
        products = ProductUi.Sample,
        date = "June 20, 2025",
        showImpact = true,
        totalImpact = "$600.00"
    )

    val NoImpact = WithImpact.copy(
        stockType = StockUi.THREE_SEVENTEEN,
        showImpact = false,
        totalProducts = "16"
    )
}
