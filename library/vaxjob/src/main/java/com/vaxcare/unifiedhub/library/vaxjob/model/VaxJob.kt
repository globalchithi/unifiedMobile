package com.vaxcare.unifiedhub.library.vaxjob.model

/**
 * Interface for Job to be run
 */
interface VaxJob {
    /**
     * Execute the job
     *
     * @param parameter Optional parameter
     */
    suspend fun execute(parameter: Any? = null)
}
