package com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.metric

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent

data class WrongProductPromptMetric(
    val ndc: String,
    val displayedMessage: String
) : TrackableEvent {
    override val name: String = "WrongProductPrompted"
    override val props: AnalyticsProps
        get() = super.props.toMutableMap().apply {
            put("ndc", ndc)
            put("displayedMessage", displayedMessage)
        }
}
