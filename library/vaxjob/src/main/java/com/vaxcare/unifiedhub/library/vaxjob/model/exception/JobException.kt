package com.vaxcare.unifiedhub.library.vaxjob.model.exception

class JobException(
    val jobName: String,
    val exception: Exception? = null
) : Exception() {
    override val message: String
        get() = "A problem occurred with VaxJob $jobName: ${exception?.message}"
}
