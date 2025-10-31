package com.vaxcare.unifiedhub.jobs.provider

import androidx.work.WorkManager
import com.vaxcare.unifiedhub.core.common.VaxJobArgument
import com.vaxcare.unifiedhub.core.common.VaxJobName
import com.vaxcare.unifiedhub.library.vaxjob.provider.BaseVaxJobProvider
import com.vaxcare.unifiedhub.worker.OneTimeParams
import com.vaxcare.unifiedhub.worker.OneTimeWorker
import com.vaxcare.unifiedhub.worker.args.InsertLotNumbersJobArgs
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnifiedVaxJobProvider @Inject constructor(
    private val workManager: WorkManager
) : BaseVaxJobProvider() {
    override fun runJobWithArgs(args: Map<String, Any?>) {
        Timber.d("runJobWithArgs - supplied arguments $args")
        if (args[VaxJobArgument.JOB_NAME] == VaxJobName.INSERT_LOT_NUMBERS_JOB) {
            val lotNumber = args[VaxJobArgument.LOT_NUMBER] as? String
            val productId = args[VaxJobArgument.PRODUCT_ID] as? Int
            val expiration = args[VaxJobArgument.EXPIRATION] as? LocalDate
            val sourceId = args[VaxJobArgument.ADD_SOURCE_ID] as? Int
            OneTimeWorker.buildOneTimeUniqueWorker(
                wm = workManager,
                OneTimeParams.InsertLotNumbers(
                    params = InsertLotNumbersJobArgs(
                        lotNumber = lotNumber,
                        epProductId = productId,
                        expiration = expiration,
                        source = sourceId
                    )
                )
            )
        }
    }
}
