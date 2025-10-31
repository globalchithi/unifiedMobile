package com.vaxcare.unifiedhub.jobs

import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.model.lot.LotNumberSource
import com.vaxcare.unifiedhub.feature.admin.metric.inventory.LotUploadEvent
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.vaxjob.model.BaseVaxJob
import com.vaxcare.unifiedhub.worker.args.InsertLotNumbersJobArgs
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Active job - Called only when sending a new lot to the backend
 */
@Singleton
class InsertLotNumbersJob @Inject constructor(
    private val lotRepository: LotRepository,
    private val analyticsRepository: AnalyticsRepository
) : BaseVaxJob(analyticsRepository) {
    override suspend fun doWork(parameter: Any?) {
        val args = parameter as? InsertLotNumbersJobArgs ?: InsertLotNumbersJobArgs()
        val lotNumber = args.lotNumber
        val productId = args.epProductId
        val expiration = args.expiration
        val source = args.source ?: LotNumberSource.VaxHubScan.id
        expiration?.let {
            try {
                if (!lotNumber.isNullOrEmpty() &&
                    (productId ?: -1) != -1 &&
                    (expiration > LocalDate.MIN)
                ) {
                    lotRepository.postLot(
                        expirationDate = expiration,
                        lotNumber = lotNumber,
                        productId = productId ?: -1,
                        isCalledByJob = true,
                        unreviewed = source != LotNumberSource.VaxHubScan.id,
                        source = source
                    )
                } else {
                    val msg =
                        "Error with inserting Lot Number: {$lotNumber} | productId: $productId | expirationDate: $expiration | sourceId: $source"
                    failure(Exception(msg), msg)
                }
            } catch (e: Exception) {
                if (retries > MAX_RETRIES - 1) {
                    analyticsRepository.track(
                        LotUploadEvent.LotUploadFailed(
                            lotNumber = lotNumber ?: "",
                            expiration = expiration,
                            productId = productId ?: -1
                        )
                    )
                }
                throw e
            }
        } ?: kotlin.run { throw Exception("Expiration Date cannot be null (Lot $lotNumber)") }
    }
}
