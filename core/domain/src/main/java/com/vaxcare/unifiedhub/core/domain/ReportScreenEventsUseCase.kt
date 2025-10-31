package com.vaxcare.unifiedhub.core.domain

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.analytics.events.CommonAnalyticsEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportScreenEventsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    suspend operator fun invoke(screenName: String) {
        analyticsRepository.track(CommonAnalyticsEvent.ScreenView(screenName))
    }
}
