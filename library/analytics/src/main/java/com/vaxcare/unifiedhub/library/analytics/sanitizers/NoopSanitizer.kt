package com.vaxcare.unifiedhub.library.analytics.sanitizers

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsSanitizer
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent
import javax.inject.Inject

class NoopSanitizer @Inject constructor() : AnalyticsSanitizer {
    override fun sanitize(event: TrackableEvent): TrackableEvent = event
}
