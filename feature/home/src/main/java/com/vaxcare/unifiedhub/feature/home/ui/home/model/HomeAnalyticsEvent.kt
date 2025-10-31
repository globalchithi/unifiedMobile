package com.vaxcare.unifiedhub.feature.home.ui.home.model

import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent

sealed interface HomeAnalyticsEvent : TrackableEvent {
    data object InAppUpdateLaunched : HomeAnalyticsEvent {
        override val name = "In-app update screen launched"
    }

    data object InAppUpdateFailed : HomeAnalyticsEvent {
        override val name = "In-app update failed"
    }

    data object InAppUpdateCompleted : HomeAnalyticsEvent {
        override val name = "In-app update completed"
    }
}
