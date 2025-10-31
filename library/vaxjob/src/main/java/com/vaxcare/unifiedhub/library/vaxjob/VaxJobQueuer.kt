package com.vaxcare.unifiedhub.library.vaxjob

import com.vaxcare.unifiedhub.library.vaxjob.service.JobSelector
import javax.inject.Inject

/**
 * Concrete implementation of [JobQueuer] that uses the existing [JobSelector].
 */
class VaxJobQueuer @Inject constructor(
    private val jobSelector: JobSelector
) : JobQueuer {
    override suspend fun queueJob(eventType: String, payload: String?) {
        jobSelector.queueJob(eventType, payload)
    }
}
