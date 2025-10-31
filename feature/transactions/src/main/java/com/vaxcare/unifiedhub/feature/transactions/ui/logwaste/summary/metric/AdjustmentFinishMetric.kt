package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.metric

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent

data class AdjustmentFinishMetric(
    val result: AdjustmentResult,
    val productCount: Int,
    val doseCount: Int,
    val financialImpact: Number,
) : TrackableEvent {
    override val name: String = "Adjustment.Finish"

    enum class AdjustmentResult {
        SUBMITTED,
        ERROR,
        ABANDONED
    }

    override val props: AnalyticsProps
        get() = super.props.toMutableMap().apply {
            put("result", result.name)
            put("productCount", productCount.toString())
            put("doseCount", doseCount.toString())
            put("financialImpact", financialImpact.toString())
        }
}
