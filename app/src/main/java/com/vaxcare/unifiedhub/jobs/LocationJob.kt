package com.vaxcare.unifiedhub.jobs

import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.vaxjob.model.BaseVaxJob
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationJob @Inject constructor(
    private val locationRepository: LocationRepository,
    analyticsRepository: AnalyticsRepository
) : BaseVaxJob(analyticsRepository) {
    companion object {
        const val LOCATION_JOB_NAME = "LocationJob"
    }

    override suspend fun doWork(parameter: Any?) {
        locationRepository.sync()
    }
}
