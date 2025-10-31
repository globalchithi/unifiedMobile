package com.vaxcare.unifiedhub.feature.transactions.addpublic.summary

import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.addpublic.AddPublicSharedTestData
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary.AddPublicSummaryDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary.AddPublicSummaryState
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.metric.AdjustmentFinishMetric

object AddPublicSummaryTestData {
    object Initial {
        val expectedUiState = AddPublicSummaryState(
            stock = StockUi.VFC,
            products = AddPublicSharedTestData.mockProductsUi,
            total = 18
        )
    }

    object SubmitSuccess {
        val initialState = Initial.expectedUiState
        val expectedMetric = AdjustmentFinishMetric(
            result = AdjustmentFinishMetric.AdjustmentResult.SUBMITTED,
            productCount = 2,
            doseCount = -18,
            financialImpact = 0.0
        )
    }

    object SubmitFailure {
        val initialState = Initial.expectedUiState
        val expectedMetric = AdjustmentFinishMetric(
            result = AdjustmentFinishMetric.AdjustmentResult.ERROR,
            productCount = 2,
            doseCount = -18,
            financialImpact = 0.0
        )
        val expectedState = initialState.copy(
            isLoading = false,
            activeDialog = AddPublicSummaryDialog.SubmissionFailed,
        )
    }

    object DismissDialog {
        val initialState = Initial.expectedUiState.copy(
            activeDialog = AddPublicSummaryDialog.SubmissionFailed
        )
        val expectedState = initialState.copy(
            activeDialog = null
        )
    }
}
