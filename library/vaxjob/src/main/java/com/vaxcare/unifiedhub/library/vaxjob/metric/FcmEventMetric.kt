package com.vaxcare.unifiedhub.library.vaxjob.metric

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent

data class FcmEventMetric(
    val fcmEventId: String?,
    val eventType: String?
) : TrackableEvent {
    override val name: String = "FcmEventMetric"

    override val props: AnalyticsProps
        get() = mapOf(
            "vaxCareEventType" to "FCM",
            "vaxCareEventId" to fcmEventId.toString(),
            "eventType" to eventType.toString(),
        )
}
