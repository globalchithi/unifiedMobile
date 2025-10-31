package com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit

import com.vaxcare.unifiedhub.core.common.ext.toUSD
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.model.ProductUi
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

object CountsSubmitSampleData {
    private val sampleProducts = (0..8).map {
        val unitPrice = it * 10
        val delta = if (it % 2 == 1) {
            it * 2
        } else {
            it * -2
        }
        ProductUi(
            id = 1,
            antigen = "EXAMPLE",
            prettyName = "Examplegen",
            quantity = it * 4,
            unitPrice = unitPrice.toUSD(),
            delta = delta,
            impact = (unitPrice * delta).toUSD(),
            presentationIcon = DesignSystemR.drawable.ic_presentation_syringe
        )
    }
    val Default = CountsSubmitState(
        stockType = StockUi.PRIVATE,
        nonSeasonalProducts = sampleProducts.take(4),
        seasonalProducts = sampleProducts.takeLast(4),
        subTotal = "$9,999.00"
    )
    val PublicStock = CountsSubmitState(
        stockType = StockUi.VFC,
        nonSeasonalProducts = sampleProducts.take(4),
        seasonalProducts = sampleProducts.takeLast(4),
        showImpact = false
    )
}
