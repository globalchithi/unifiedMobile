package com.vaxcare.unifiedhub.library.vaxjob.model

import com.vaxcare.unifiedhub.library.vaxjob.model.exception.JobException
import com.vaxcare.unifiedhub.library.vaxjob.model.exception.JobLimitException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Executor interface for jobs
 */
interface JobExecutor {
    /**
     * Executes all jobs from job list asynchronously
     */
    suspend fun executeJobs(param: Any? = null)

    fun onFinished()
}

const val MAX_RUN_ATTEMPTS = 3

class JobExecutorImpl(
    jobs: List<VaxJob>
) : JobExecutor {
    private val runningJobs = jobs.map { JobProgress(it) }.toMutableList()

    override suspend fun executeJobs(param: Any?) {
        withContext(Dispatchers.IO) {
            runningJobs
                .filter { !it.completed && it.canRun() }
                .map {
                    async {
                        try {
                            Timber.i("executing job: ${it.vaxJob::class.simpleName}")
                            it.vaxJob.execute(param)
                            it.completed = true
                        } catch (jobException: JobException) {
                            Timber.e(jobException)
                            runningJobs.find { it.jobName == jobException.jobName }?.let {
                                it.runAttempts++
                            } ?: run { throw jobException }
                        } catch (e: Exception) {
                            coroutineContext.ensureActive()
                            Timber.e(e)
                        }
                        it
                    }
                }.awaitAll()
                .filter { !it.completed }
                .also { list ->
                    list.firstOrNull { !it.canRun() }?.let {
                        runningJobs.merge(list)
                        val jobNames = list.map { it.jobName }.joinToString(",")
                        throw JobLimitException(jobNames)
                    }
                }.filter { it.canRun() }
                .also { list ->
                    list.firstOrNull()?.let {
                        runningJobs.merge(list)
                        throw JobException(it.jobName ?: "")
                    }
                }
        }
    }

    override fun onFinished() {
        runningJobs.reset()
    }

    private fun List<JobProgress>.reset() = forEach { it.reset() }

    private fun List<JobProgress>.merge(other: List<JobProgress>) {
        forEach { job -> other.find { it.jobName == job.jobName }?.let { job.merge(it) } }
    }

    private data class JobProgress(
        val vaxJob: VaxJob,
        var runAttempts: Int = 0,
        var completed: Boolean = false
    ) {
        val jobName = vaxJob::class.simpleName

        fun reset() {
            runAttempts = 0
            completed = false
        }

        fun canRun() = runAttempts < MAX_RUN_ATTEMPTS

        fun merge(other: JobProgress) {
            this.runAttempts = other.runAttempts
            this.completed = other.completed
        }
    }
}
