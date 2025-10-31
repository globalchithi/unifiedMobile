package com.vaxcare.unifiedhub.jobs

import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.vaxjob.model.BaseVaxJob
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 30 minute and active job - Called via FCM when eventType is
 * "com.vaxcare.vaxhub.firebase.SYNC.LOT_NUMBERS" for resyncing LotNumbers from backend
 */
@Singleton
class LotNumbersJob @Inject constructor(
    private val lotRepository: LotRepository,
    analyticsRepository: AnalyticsRepository
) : BaseVaxJob(analyticsRepository) {
    companion object {
        const val LOT_NUMBERS_JOB_NAME = "LotNumbersJob"
    }

    override suspend fun doWork(parameter: Any?) {
        lotRepository.syncLots(isCalledByJob = true)
    }
}
