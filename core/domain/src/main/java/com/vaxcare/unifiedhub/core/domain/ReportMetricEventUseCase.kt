package com.vaxcare.unifiedhub.core.domain

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportMetricEventUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    suspend operator fun invoke(vararg metrics: TrackableEvent) {
        analyticsRepository.track(*metrics)
    }
}
