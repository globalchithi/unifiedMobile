package com.vaxcare.unifiedhub.jobs

import com.vaxcare.unifiedhub.core.data.repository.ClinicRepository
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.vaxjob.model.BaseVaxJob
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Called for syncing all clinics. 24 hour cadence
 */
@Singleton
class ClinicJob @Inject constructor(
    private val clinicRepository: ClinicRepository,
    analyticsRepository: AnalyticsRepository
) : BaseVaxJob(analyticsRepository) {
    override suspend fun doWork(parameter: Any?) {
        clinicRepository.syncClinics(isCalledByJob = true)
    }
}
