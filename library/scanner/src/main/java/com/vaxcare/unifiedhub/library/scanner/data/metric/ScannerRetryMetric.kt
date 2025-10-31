package com.vaxcare.unifiedhub.library.scanner.data.metric

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent

data class ScannerRetryMetric(
    val valid: Boolean,
    val reason: String
) : TrackableEvent {
    override val name = "ScannerRetry"
    override val props: AnalyticsProps
        get() = super.props.toMutableMap().apply {
            put("valid", valid.toString())
            put("reason", reason)
        }
}
