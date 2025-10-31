package com.vaxcare.unifiedhub.feature.transactions.counts.confirm

import com.vaxcare.unifiedhub.core.common.ext.toStandardDate
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.navigation.CountsCompleteRoute
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete.CountsCompleteState
import java.time.LocalDate
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

object CountsCompleteTestData {
    object NoVariance {
        val navRoute = CountsCompleteRoute(
            stockType = StockType.VFC,
            products = 9,
            units = 99,
        )

        val expectedUiState = CountsCompleteState(
            stockType = StockUi.VFC,
            date = LocalDate.now().toStandardDate(),
            showImpact = false,
            showVariance = false,
            totalProducts = 9,
            totalUnits = 99,
            inventoryBalance = "$10,000.00"
        )
    }

    object VarianceWithPositiveImpact {
        val navRoute = CountsCompleteRoute(
            stockType = StockType.PRIVATE,
            addedUnits = 20,
            addedImpact = 200F,
            missingUnits = 10,
            missingImpact = -100F
        )

        val expectedUiState = CountsCompleteState(
            stockType = StockUi.PRIVATE,
            date = LocalDate.now().toStandardDate(),
            showImpact = true,
            showVariance = true,
            disclaimerRes = DesignSystemR.string.payment_disclaimer,
            addedUnits = 20,
            addedImpact = "$200.00",
            missingUnits = 10,
            missingImpact = "-$100.00",
            totalImpact = "$100.00",
            inventoryBalance = "$5,100.00"
        )
    }

    object VarianceWithNegativeImpact {
        val navRoute = CountsCompleteRoute(
            stockType = StockType.PRIVATE,
            addedUnits = 10,
            addedImpact = 100F,
            missingUnits = 20,
            missingImpact = -200F
        )

        val expectedUiState = CountsCompleteState(
            stockType = StockUi.PRIVATE,
            date = LocalDate.now().toStandardDate(),
            showImpact = true,
            showVariance = true,
            disclaimerRes = DesignSystemR.string.invoice_disclaimer,
            addedUnits = 10,
            addedImpact = "$100.00",
            missingUnits = 20,
            missingImpact = "-$200.00",
            totalImpact = "-$100.00",
            inventoryBalance = "$4,900.00"
        )
    }

    object VarianceWithNoImpact {
        val navRoute = CountsCompleteRoute(
            stockType = StockType.VFC,
            addedUnits = 10,
            missingUnits = 20,
        )

        val expectedUiState = CountsCompleteState(
            stockType = StockUi.VFC,
            date = LocalDate.now().toStandardDate(),
            showImpact = false,
            showVariance = true,
            addedUnits = 10,
            missingUnits = 20,
            inventoryBalance = "$10,000.00"
        )
    }
}
