package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.complete

import com.vaxcare.unifiedhub.core.common.ext.toStandardDate
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUi
import java.time.LocalDate

object AddPublicCompleteSampleData {
    val Default = AddPublicCompleteState(
        stockType = StockUi.THREE_SEVENTEEN,
        products = ProductUi.Sample,
        date = LocalDate.now().toStandardDate(),
        totalProducts = ProductUi.Sample.sumOf { it.getTotal() }.toString()
    )
}
