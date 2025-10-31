package com.vaxcare.unifiedhub.library.analytics.reporters

import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.MetricsReporter
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent
import org.json.JSONObject
import javax.inject.Inject

class MixpanelReporter @Inject constructor(
    private val mixpanel: MixpanelAPI
) : MetricsReporter {
    override suspend fun track(event: TrackableEvent) {
        mixpanel.track(event.name, event.props.toJson())
    }

    private fun AnalyticsProps.toJson() = JSONObject(this)
}
