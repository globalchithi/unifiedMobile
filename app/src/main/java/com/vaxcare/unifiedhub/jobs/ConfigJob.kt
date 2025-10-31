package com.vaxcare.unifiedhub.jobs

import com.vaxcare.unifiedhub.core.data.repository.ConfigRepository
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.vaxjob.model.BaseVaxJob
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigJob @Inject constructor(
    private val configRepository: ConfigRepository,
    analyticsRepository: AnalyticsRepository
) : BaseVaxJob(analyticsRepository) {
    companion object {
        const val CONFIG_JOB_NAME = "ConfigJob"
    }

    override suspend fun doWork(parameter: Any?) {
        configRepository.upsertSetupConfig(isCalledByJob = true)
    }
}
