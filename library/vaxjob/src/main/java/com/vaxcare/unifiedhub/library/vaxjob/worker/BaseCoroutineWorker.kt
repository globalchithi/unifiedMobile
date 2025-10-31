package com.vaxcare.unifiedhub.library.vaxjob.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import timber.log.Timber

const val MAX_RETRIES = 3

abstract class BaseCoroutineWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    protected val workManager = WorkManager.getInstance(context)
    protected abstract val analyticsRepository: AnalyticsRepository

    protected open fun finishAndResult(result: Result): Result = result

    protected fun retry(
        payload: Data? = null,
        exception: Exception = Exception(),
        message: String? = null,
    ): Result {
        Timber.e(exception, message ?: "Error running worker")

        if (runAttemptCount < MAX_RETRIES) {
            return Result.retry()
        }

        return if (payload != null) {
            finishAndResult(Result.failure(payload))
        } else {
            finishAndResult(Result.failure())
        }
    }
}
