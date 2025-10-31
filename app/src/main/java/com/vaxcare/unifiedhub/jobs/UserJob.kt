package com.vaxcare.unifiedhub.jobs

import com.vaxcare.unifiedhub.core.data.repository.UserRepository
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.vaxjob.model.BaseVaxJob
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserJob @Inject constructor(
    private val userRepository: UserRepository,
    analyticsRepository: AnalyticsRepository
) : BaseVaxJob(analyticsRepository) {
    override suspend fun doWork(parameter: Any?) {
        userRepository.forceSyncUsers(true)
    }
}
