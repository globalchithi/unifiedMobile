package com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete

import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

object CountsCompleteSampleData {
    val WithImpact = CountsCompleteState(
        showImpact = true,
        disclaimerRes = DesignSystemR.string.payment_disclaimer,
        date = "June 20, 2025",
        inventoryBalance = "$5,999.99",
        addedUnits = 47,
        addedImpact = "$699.99",
        missingUnits = 10,
        missingImpact = "-$99.99",
        totalImpact = "$600.00"
    )
    val NoImpact = CountsCompleteState(
        showImpact = false,
        date = "July 4, 2025",
        inventoryBalance = "$9,999.99",
        totalProducts = 8,
        totalUnits = 114,
    )
}
