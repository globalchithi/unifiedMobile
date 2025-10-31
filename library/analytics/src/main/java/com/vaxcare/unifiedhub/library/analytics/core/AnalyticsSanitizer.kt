package com.vaxcare.unifiedhub.library.analytics.core

interface AnalyticsSanitizer {
    fun sanitize(event: TrackableEvent): TrackableEvent
}
