package com.vaxcare.unifiedhub.library.vaxjob.metric

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent
import java.time.LocalDateTime

class JobMetric(
    vaxJobName: String,
    private val uniqueId: String,
    private val retries: Int,
    private val success: Boolean,
    private val dateExecuted: LocalDateTime
) : TrackableEvent {
    override val name: String = "Job.$vaxJobName"

    override val props: AnalyticsProps
        get() = mapOf(
            "uniqueId" to uniqueId,
            "retries" to retries.toString(),
            "success" to success.toString(),
            "dateExecuted" to dateExecuted.toString()
        )
}
