package com.vaxcare.unifiedhub.library.analytics.reporters

import com.datadog.android.rum.GlobalRumMonitor
import com.datadog.android.rum.RumActionType
import com.vaxcare.unifiedhub.library.analytics.core.MetricsReporter
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent
import javax.inject.Inject

class DatadogReporter @Inject constructor() : MetricsReporter {
    override suspend fun track(event: TrackableEvent) {
        GlobalRumMonitor.get().addAction(
            type = RumActionType.CUSTOM,
            name = event.name,
            attributes = event.props
        )
    }
}
