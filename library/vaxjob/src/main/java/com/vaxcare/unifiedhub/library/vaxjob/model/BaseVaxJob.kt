package com.vaxcare.unifiedhub.library.vaxjob.model

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.analytics.reporters.DatadogReporter
import com.vaxcare.unifiedhub.library.analytics.reporters.MixpanelReporter
import com.vaxcare.unifiedhub.library.vaxjob.metric.JobMetric
import com.vaxcare.unifiedhub.library.vaxjob.metric.JobStartMetric
import com.vaxcare.unifiedhub.library.vaxjob.model.exception.JobException
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext
import kotlin.math.pow

/**
 * Base class for VaxJobs. Extend this class for new VaxJob implementations.
 * For more info: https://vaxcare.atlassian.net/wiki/spaces/CSP/pages/2651127821/VaxJobs
 */
abstract class BaseVaxJob(
    private val analyticsRepository: AnalyticsRepository
) : VaxJob {
    companion object {
        /**
         * Max retry attempts
         */
        const val MAX_RETRIES = 3

        /**
         * Delay time between retries (seconds)
         */
        private const val RETRY_DELAY = 3.0
    }

    /**
     * Counter variable for retries
     */
    protected var retries: Int = 0

    /**
     * Date this job was created
     */
    private var dateStarted: LocalDateTime? = null
    private lateinit var jobUniqueId: UUID

    /**
     * The work (actual "job") will execute in this abstract function
     *
     * @param parameter Optional parameter specific to the job
     */
    abstract suspend fun doWork(parameter: Any? = null)

    override suspend fun execute(parameter: Any?) =
        try {
            if (dateStarted == null) {
                dateStarted = LocalDateTime.now()
                jobUniqueId = UUID.randomUUID()
                sendStartMetric()
            }
            doWork(parameter)
            finalizeJob(true)
        } catch (e: Exception) {
            if (e is CancellationException) {
                finalizeJob(false)
                coroutineContext.ensureActive()
            } else {
                retry(exception = e, message = e.message, parameter = parameter)
            }
        }

    /**
     * Retry function - extend if different behavior is desired
     *
     * @param exception Exception that was thrown from doWork
     * @param message Message from exception/failure from execute
     * @param parameter original parameter passed from execute
     */
    open suspend fun retry(
        exception: Exception,
        message: String?,
        parameter: Any? = null
    ) {
        if (retries++ < MAX_RETRIES) {
            val exp = RETRY_DELAY.pow(retries.toDouble())
            delay(TimeUnit.SECONDS.toMillis(exp.toLong()))
            execute(parameter)
        } else {
            failure(exception, message)
        }
    }

    /**
     * Failure - extend if different behavior is desired
     *
     * @param exception Exception thrown causing the failure
     * @param message Message passed from retry
     */
    open suspend fun failure(exception: Exception, message: String?) {
        Timber.e(exception, message)
        finalizeJob(false, exception)
    }

    /**
     * Sends JobStartMetric
     */
    private suspend fun sendStartMetric() {
        analyticsRepository.track(
            JobStartMetric(
                vaxJobName = this::class.java.simpleName,
                uniqueId = jobUniqueId.toString(),
                dateStarted = dateStarted ?: LocalDateTime.now()
            )
        ) { it !is MixpanelReporter && it !is DatadogReporter }
    }

    /**
     * Saves JobMetric and resets variables
     *
     * @param success If job was successful or not
     */
    private suspend fun finalizeJob(success: Boolean, exception: Exception? = null) {
        val jobName = this::class.java.simpleName
        analyticsRepository.track(
            JobMetric(
                uniqueId = jobUniqueId.toString(),
                retries = retries,
                success = success,
                dateExecuted = dateStarted ?: LocalDateTime.now(),
                vaxJobName = jobName
            )
        ) { it !is MixpanelReporter && it !is DatadogReporter }

        retries = 0
        dateStarted = null
        if (exception != null) {
            throw JobException(jobName, exception)
        }
    }
}
