package com.vaxcare.unifiedhub.library.vaxjob

/**
 * An abstraction for enqueuing background jobs based on an event type.
 * This decouples the message handling logic from the specific implementation
 * of the job execution system (e.g., VaxJobs, WorkManager).
 */
interface JobQueuer {
    suspend fun queueJob(eventType: String, payload: String?)
}
