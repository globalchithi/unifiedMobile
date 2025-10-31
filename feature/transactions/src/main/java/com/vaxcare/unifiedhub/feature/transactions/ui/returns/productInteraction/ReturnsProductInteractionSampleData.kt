package com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction

import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductLotUi
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason.ReturnReasonUi

object ReturnsProductInteractionSampleData {
    val Default = ReturnsProductInteractionState(
        isLoading = false,
        lots = ProductLotUi.Sample,
        reason = ReturnReasonUi.EXPIRED,
        isScannerActive = false,
    )
}
