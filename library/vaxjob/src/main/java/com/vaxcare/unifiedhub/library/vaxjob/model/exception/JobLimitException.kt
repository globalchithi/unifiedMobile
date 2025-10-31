package com.vaxcare.unifiedhub.library.vaxjob.model.exception

class JobLimitException(
    val jobNames: String
) : Exception() {
    override val message: String
        get() = "jobs hit the limit: $jobNames / ${super.message}"
}
