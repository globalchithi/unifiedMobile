package com.vaxcare.unifiedhub.library.analytics.core

interface AnalyticsRepository {
    suspend fun track(vararg events: TrackableEvent, filter: (MetricsReporter) -> Boolean = { true })
}
