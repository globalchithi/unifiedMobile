package com.vaxcare.unifiedhub.jobs

import com.vaxcare.unifiedhub.core.data.repository.WrongProductNdcRepository
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.vaxjob.model.BaseVaxJob
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Called for syncing black listed product ndcs from backend. 24 hour cadence
 */
@Singleton
class WrongProductNdcJob @Inject constructor(
    private val repository: WrongProductNdcRepository,
    analyticsRepository: AnalyticsRepository
) : BaseVaxJob(analyticsRepository) {
    override suspend fun doWork(parameter: Any?) {
        repository.getAndUpsertWrongProductNdcs(isCalledByJob = true)
    }
}
