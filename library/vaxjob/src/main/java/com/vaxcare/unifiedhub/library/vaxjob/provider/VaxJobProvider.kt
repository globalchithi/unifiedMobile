package com.vaxcare.unifiedhub.library.vaxjob.provider

/**
 * Interface for the provider that will manage providing VaxJobs with certain arguments
 */
interface VaxJobProvider {
    /**
     * Add a collection of key-value pairs and return this
     *
     * @param args arguments which keys are defined in Constants.kt
     * @return this
     */
    fun withArguments(vararg args: Pair<String, Any?>): VaxJobProvider

    /**
     * Function to run job with supplied arguments
     */
    fun runJob()
}
