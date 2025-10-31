package com.vaxcare.unifiedhub.library.analytics.enrichers

import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsEnricher
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SessionAnalyticsEnricher @Inject constructor(
    private val userSessionPreferenceDataSource: UserSessionPreferenceDataSource
) : AnalyticsEnricher {
    override suspend fun defaultProps(): AnalyticsProps =
        with(userSessionPreferenceDataSource) {
            mapOf(
                "userId" to userId.firstOrNull().toString(),
                "sessionId" to sessionId.firstOrNull().toString()
            )
        }
}
