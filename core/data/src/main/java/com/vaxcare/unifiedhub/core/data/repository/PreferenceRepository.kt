package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import com.vaxcare.unifiedhub.core.datastore.datasource.UsagePreferenceDataSource
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import javax.inject.Inject

class PreferenceRepository @Inject constructor(
    private val locationPreferences: LocationPreferenceDataSource,
    private val sessionPreferences: UserSessionPreferenceDataSource,
    private val usagePreferences: UsagePreferenceDataSource
) {
    suspend fun clearPreferences() {
        locationPreferences.clear()
        sessionPreferences.clear()
        usagePreferences.clearLastSelectedStock()
    }
}
