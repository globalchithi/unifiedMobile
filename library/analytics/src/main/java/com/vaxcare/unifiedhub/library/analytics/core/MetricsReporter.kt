package com.vaxcare.unifiedhub.library.analytics.core

fun interface MetricsReporter {
    suspend fun track(event: TrackableEvent)
}
