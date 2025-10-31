package com.vaxcare.unifiedhub.library.analytics.core

typealias AnalyticsProps = Map<String, String>

interface TrackableEvent {
    val name: String
    val props: AnalyticsProps get() = emptyMap()
}
