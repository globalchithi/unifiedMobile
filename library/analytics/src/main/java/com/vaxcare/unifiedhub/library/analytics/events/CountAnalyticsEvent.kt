package com.vaxcare.unifiedhub.library.analytics.events

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent
import kotlin.collections.buildMap

// Example of events for counts. This should be on it's own module
sealed interface CountAnalyticsEvent : TrackableEvent {
    data object CountStarted : CountAnalyticsEvent {
        override val name = "Count Started"
    }

    data class CountCompleted(
        val dosesCounted: Int
    ) : CountAnalyticsEvent {
        override val name = "Count Completed"
        override val props: AnalyticsProps = buildMap {
            "dosesCounted" to dosesCounted
        }
    }
}
