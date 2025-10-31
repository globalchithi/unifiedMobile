package com.vaxcare.unifiedhub.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.vaxcare.unifiedhub.BuildConfig
import com.vaxcare.unifiedhub.core.common.VaxJobName.INSERT_LOT_NUMBERS_JOB
import com.vaxcare.unifiedhub.core.data.adapter.TimeAdapter
import com.vaxcare.unifiedhub.core.model.lot.LotNumberSource
import com.vaxcare.unifiedhub.jobs.InsertLotNumbersJob
import com.vaxcare.unifiedhub.jobs.LocationJob
import com.vaxcare.unifiedhub.jobs.LocationJob.Companion.LOCATION_JOB_NAME
import com.vaxcare.unifiedhub.jobs.LotInventoryJob
import com.vaxcare.unifiedhub.jobs.LotInventoryJob.Companion.LOT_INVENTORY_JOB_NAME
import com.vaxcare.unifiedhub.jobs.LotNumbersJob
import com.vaxcare.unifiedhub.jobs.LotNumbersJob.Companion.LOT_NUMBERS_JOB_NAME
import com.vaxcare.unifiedhub.jobs.PingJob
import com.vaxcare.unifiedhub.jobs.PingJob.Companion.PING_JOB_NAME
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.vaxjob.worker.BaseCoroutineWorker
import com.vaxcare.unifiedhub.worker.args.InsertLotNumbersJobArgs
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltWorker
class OneTimeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    override val analyticsRepository: AnalyticsRepository
) : BaseCoroutineWorker(context, params) {
    companion object {
        private const val ONETIME_WORKER =
            "${BuildConfig.APPLICATION_ID}.WORKER.ONETIME.WORKER."

        /**
         * Enqueue the worker to be executed in the next second
         *
         * @param wm the [WorkManager] instance
         */
        fun buildOneTimeUniqueWorker(wm: WorkManager, parameters: OneTimeParams) {
            val inputArgs = mutableMapOf<String, Any>("jobName" to parameters.name)
            inputArgs.putAll(parameters.args)
            val databuilder = Data.Builder().putAll(inputArgs)
            val suffix = if (parameters.shouldAddUniqueId) UUID.randomUUID() else ""
            val jobId = "$ONETIME_WORKER${parameters.name}$suffix"
            val requestBuilder = OneTimeWorkRequestBuilder<OneTimeWorker>()
                .setInitialDelay(1000, TimeUnit.MILLISECONDS)
                .setInputData(databuilder.build())

            parameters.constraints?.let { constraints ->
                requestBuilder.setConstraints(constraints)
            }

            val oneTimeWorkRequest = requestBuilder.build()

            wm.enqueueUniqueWork(
                jobId,
                ExistingWorkPolicy.KEEP,
                oneTimeWorkRequest
            )
        }
    }

    @Inject
    lateinit var pingJob: PingJob

    @Inject
    lateinit var locationJob: LocationJob

    @Inject
    lateinit var insertLotNumbersJob: InsertLotNumbersJob

    @Inject
    lateinit var lotInventoryJob: LotInventoryJob

    @Inject
    lateinit var lotNumbersJob: LotNumbersJob

    override suspend fun doWork(): Result {
        val (job, args) = when (inputData.getString("jobName")) {
            PING_JOB_NAME -> pingJob to null
            LOCATION_JOB_NAME -> locationJob to null
            INSERT_LOT_NUMBERS_JOB -> {
                val param = InsertLotNumbersJobArgs(
                    lotNumber = inputData.getString("LotNumber"),
                    epProductId = inputData.getString("productId")?.toInt(),
                    expiration = TimeAdapter().stringToLocalDate(
                        inputData.getString("expiration") ?: ""
                    ),
                    source = inputData.getString("source")?.toInt()
                        ?: LotNumberSource.VaxHubScan.id
                )
                insertLotNumbersJob to param
            }

            LOT_INVENTORY_JOB_NAME -> lotInventoryJob to null
            LOT_NUMBERS_JOB_NAME -> lotNumbersJob to null

            else -> null to null
        }

        job?.execute(args)
        return Result.success()
    }
}

private val networkConstraints = Constraints
    .Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

/**
 * Parameters for specific on demand jobs with optional parameters
 *
 * @property name name of VaxJob
 * @property args args for VaxJob
 * @property shouldAddUniqueId whether to add a UUID to the suffix
 * @property constraints needed for worker to run
 */
sealed class OneTimeParams(
    val name: String,
    val args: Map<String, Any>,
    val shouldAddUniqueId: Boolean = true,
    val constraints: Constraints? = null
) {
    data object PingJob : OneTimeParams(
        name = PING_JOB_NAME,
        args = emptyMap(),
        constraints = networkConstraints,
    )

    data object Location : OneTimeParams(
        name = LOCATION_JOB_NAME,
        args = emptyMap(),
        constraints = networkConstraints
    )

    data object LotInventoryJob : OneTimeParams(
        name = LOT_INVENTORY_JOB_NAME,
        args = emptyMap(),
        constraints = networkConstraints
    )

    data object LotNumbersJob : OneTimeParams(
        name = LOT_NUMBERS_JOB_NAME,
        args = emptyMap(),
        constraints = networkConstraints
    )

    data class InsertLotNumbers(
        val params: InsertLotNumbersJobArgs = InsertLotNumbersJobArgs()
    ) : OneTimeParams(
            name = "InsertLotNumbersJob",
            args = params.toMap()
        )
}
