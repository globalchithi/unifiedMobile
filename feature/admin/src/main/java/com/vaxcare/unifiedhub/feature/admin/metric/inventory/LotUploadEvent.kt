package com.vaxcare.unifiedhub.feature.admin.metric.inventory

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent
import java.time.LocalDate

sealed interface LotUploadEvent : TrackableEvent {
    data class LotUploadFailed(
        val lotNumber: String,
        val expiration: LocalDate,
        val productId: Int
    ) : LotUploadEvent {
        override val name: String
            get() = "LotUploadFailed"
        override val props: AnalyticsProps
            get() = super.props.toMutableMap().apply {
                put("lotNumber", lotNumber)
                put("expiration", expiration.toString())
                put("productId", productId.toString())
            }
    }
}
