package com.vaxcare.unifiedhub.library.analytics.enrichers

import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsEnricher
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class LocationAnalyticsEnricher @Inject constructor(
    private val locationPreferenceDataSource: LocationPreferenceDataSource
) : AnalyticsEnricher {
    override suspend fun defaultProps(): AnalyticsProps =
        with(locationPreferenceDataSource) {
            mapOf(
                "partnerId" to partnerId.firstOrNull().toString(),
                "clinicId" to parentClinicId.firstOrNull().toString(),
            )
        }
}
