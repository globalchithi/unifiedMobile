package com.vaxcare.unifiedhub.library.vaxjob.service

/**
 * Class used to create a worker and queue it up based on the event type
 */
interface JobSelector {
    suspend fun queueJob(eventType: String, payload: String? = null)
}
