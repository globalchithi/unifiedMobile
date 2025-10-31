package com.vaxcare.unifiedhub.core.common.provider

/**
 * Interface for a wrapper around a Job
 */
interface JobRunner {
    /**
     * Fires off the wrapped job with the supplied arguments
     *
     * @param args applied to wrapper job
     */
    fun runJobWithArgs(args: Map<String, Any?>)
}
