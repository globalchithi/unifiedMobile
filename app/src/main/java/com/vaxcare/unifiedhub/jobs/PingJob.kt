package com.vaxcare.unifiedhub.jobs

import com.vaxcare.unifiedhub.core.data.repository.UserRepository
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.vaxjob.model.BaseVaxJob
import javax.inject.Inject

/**
 * Job to be run from OneTimeWorker whenever a user pins in
 */
class PingJob @Inject constructor(
    private val userRepository: UserRepository,
    analyticsRepository: AnalyticsRepository
) : BaseVaxJob(analyticsRepository) {
    companion object {
        const val PING_JOB_NAME = "pingjob"
    }

    override suspend fun doWork(parameter: Any?) {
        userRepository.pingVaxCareServer()
    }
}
