package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home

import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.model.ProductUi

object AddPublicHomeSampleData {
    val Default = AddPublicHomeState(
        stockType = StockUi.VFC,
        products = ProductUi.Sample.take(4),
        isInvalidScan = true,
    )

    fun getStateForStock(stock: StockUi) =
        AddPublicHomeState(
            stockType = stock,
            products = ProductUi.Sample.take(4),
        )
}
