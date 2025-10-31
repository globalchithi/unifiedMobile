package com.vaxcare.unifiedhub.library.analytics.events

import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent

sealed interface CommonAnalyticsEvent : TrackableEvent {
    data class ScreenView(
        val screenName: String,
        val userId: String? = null
    ) : CommonAnalyticsEvent {
        override val name = "Screen Viewed"
        override val props = buildMap {
            put("screenName", screenName)
            userId?.let { put("userId", it) }
        }
    }

    data object TestButtonPressed : CommonAnalyticsEvent {
        override val name: String
            get() = "Test Button Pressed"
    }
}
