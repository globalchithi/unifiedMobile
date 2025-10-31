package com.vaxcare.unifiedhub.feature.transactions.returns.summary

import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.returns.ReturnsSharedTestData
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.metric.AdjustmentFinishMetric
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason.ReturnReasonUi
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryState

object ReturnsSummaryTestData {
    private val successMetric = AdjustmentFinishMetric(
        result = AdjustmentFinishMetric.AdjustmentResult.SUBMITTED,
        productCount = 3,
        doseCount = -78,
        financialImpact = 0.0
    )

    object Initial {
        val expectedUiState = ReturnsSummaryState(
            stock = StockUi.PRIVATE,
            reason = ReturnReasonUi.EXPIRED,
            products = ReturnsSharedTestData.mockProductsUi,
            total = 78
        )
    }

    object SubmitSuccess {
        val initialState = Initial.expectedUiState
        val expectedMetric = successMetric
    }

    object SubmitFailure {
        val initialState = Initial.expectedUiState
        val expectedMetric = AdjustmentFinishMetric(
            result = AdjustmentFinishMetric.AdjustmentResult.ERROR,
            productCount = 3,
            doseCount = -78,
            financialImpact = 0.0
        )
        val expectedState = initialState.copy(
            isLoading = false,
            activeDialog = ReturnsSummaryDialog.SubmissionFailed,
        )
    }

    object RetrySubmit {
        val initialState = Initial.expectedUiState.copy(
            activeDialog = ReturnsSummaryDialog.SubmissionFailed
        )
        val expectedMetric = successMetric
    }

    object DismissDialog {
        val initialState = Initial.expectedUiState.copy(
            activeDialog = ReturnsSummaryDialog.SubmissionFailed
        )
        val expectedState = initialState.copy(
            activeDialog = null
        )
    }
}
