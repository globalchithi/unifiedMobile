package com.vaxcare.unifiedhub.feature.transactions.ui.metric

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent

data class ReasonSelectedMetric(
    val reasonContext: String,
    val reason: String
) : TrackableEvent {
    override val name: String
        get() = "${reasonContext}Selected"

    override val props: AnalyticsProps
        get() = mapOf("reason" to reason)
}
