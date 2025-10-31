package com.vaxcare.unifiedhub.library.vaxjob.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import com.vaxcare.unifiedhub.library.vaxjob.model.JobExecutor
import com.vaxcare.unifiedhub.library.vaxjob.model.exception.JobException
import com.vaxcare.unifiedhub.library.vaxjob.model.exception.JobLimitException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.ref.WeakReference

abstract class BasePeriodicWorker(
    context: Context,
    params: WorkerParameters
) : BaseCoroutineWorker(context, params) {
    abstract val locationPreferenceDataSource: LocationPreferenceDataSource
    abstract val executor: JobExecutor
    abstract val dispatcherProvider: DispatcherProvider

    override suspend fun doWork(): Result = supervisorScope { executeJobs() }

    override fun finishAndResult(result: Result): Result {
        Timber.d("${this::class.simpleName} calling finish and result: $result")
        executor.onFinished()
        return result
    }

    private suspend fun executeJobs() =
        withContext(dispatcherProvider.io) {
            try {
                Timber.d("${this::class.simpleName} started")
                if ((locationPreferenceDataSource.parentClinicId.firstOrNull() ?: 0L) == 0L) {
                    Timber.e("${this::class.simpleName} failure - ClinicID is not set up")
                    finishAndResult(Result.failure())
                } else {
                    executor.executeJobs(WeakReference(applicationContext))
                    finishAndResult(Result.success())
                }
            } catch (jobException: JobException) {
                Timber.e(jobException)
                retry(exception = jobException, message = "Error with ${this::class.simpleName}")
            } catch (jobLimitException: JobLimitException) {
                Timber.e(jobLimitException)
                finishAndResult(Result.failure())
            } catch (e: Exception) {
                coroutineContext.ensureActive()
                Timber.e(e)
                retry(exception = e, message = "Error with ${this::class.simpleName}")
            }
        }
}
