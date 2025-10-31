package com.vaxcare.unifiedhub.feature.transactions.ui.counts.metric

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent

data class CountFinishMetric(
    val result: CountResult,
    val absoluteVariance: Int,
) : TrackableEvent {
    override val name: String = "CountInventory.Finish"

    enum class CountResult(val value: String) {
        SUBMITTED("Submitted"),
        ERROR("Error"),
        ABANDONED("Abandoned"),
    }

    override val props: AnalyticsProps
        get() = super.props.toMutableMap().apply {
            put("result", result.value)
            put("absoluteVariance", absoluteVariance.toString())
        }
}
