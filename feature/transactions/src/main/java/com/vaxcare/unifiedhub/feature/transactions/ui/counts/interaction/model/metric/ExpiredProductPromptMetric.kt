package com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.metric

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent

data class ExpiredProductPromptMetric(
    val lotNumber: String,
    val expiration: String
) : TrackableEvent {
    override val name: String = "ExpiredProductPrompted"
    override val props: AnalyticsProps
        get() = super.props.toMutableMap().apply {
            put("lotNumber", lotNumber)
            put("expiration", expiration)
        }
}
