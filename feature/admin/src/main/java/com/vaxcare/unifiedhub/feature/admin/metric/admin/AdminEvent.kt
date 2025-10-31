package com.vaxcare.unifiedhub.feature.admin.metric.admin

import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent

sealed interface AdminEvent : TrackableEvent {
    data object AdminAuthenticated : AdminEvent {
        override val name: String
            get() = "Admin Authenticated"
    }

    data object AdminAuthenticationFailed : AdminEvent {
        override val name: String
            get() = "Admin Authentication Failed"
    }
}
