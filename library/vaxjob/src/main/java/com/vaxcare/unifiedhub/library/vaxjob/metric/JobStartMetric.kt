package com.vaxcare.unifiedhub.library.vaxjob.metric

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent
import java.time.LocalDateTime

class JobStartMetric(
    private val dateStarted: LocalDateTime,
    private val uniqueId: String,
    vaxJobName: String
) : TrackableEvent {
    override val name: String = "JobStart.$vaxJobName"

    override val props: AnalyticsProps
        get() = mapOf(
            "uniqueId" to uniqueId,
            "dateExecuted" to dateStarted.toString()
        )
}
