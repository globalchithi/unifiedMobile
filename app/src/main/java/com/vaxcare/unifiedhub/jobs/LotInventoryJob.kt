package com.vaxcare.unifiedhub.jobs

import com.vaxcare.unifiedhub.core.data.repository.CountRepository
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.vaxjob.model.BaseVaxJob
import com.vaxcare.unifiedhub.worker.args.LotInventoryArgs
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 30 minute and active job - Called every half hour or via FCM when eventType is
 * "VaxHub.FirebaseEvents.CountConfirmationEvent" for resyncing the local LotInventory
 */
@Singleton
class LotInventoryJob @Inject constructor(
    private val lotInventoryRepository: LotInventoryRepository,
    private val productRepository: ProductRepository,
    private val countRepository: CountRepository,
    analyticsRepository: AnalyticsRepository
) : BaseVaxJob(analyticsRepository) {
    companion object {
        const val LOT_INVENTORY_JOB_NAME = "LotInventoryJob"
    }

    override suspend fun doWork(parameter: Any?) {
        val updateMappingsAndCount = (
            parameter as? LotInventoryArgs
                ?: LotInventoryArgs()
        ).let { it.updateMappingsAndCount ?: true }

        lotInventoryRepository.syncLotInventory(isCalledByJob = true)
        if (updateMappingsAndCount) {
            productRepository.updateProductMappings(isCalledByJob = true)
            countRepository.refreshCounts(isCalledByJob = true)
        }
    }
}
