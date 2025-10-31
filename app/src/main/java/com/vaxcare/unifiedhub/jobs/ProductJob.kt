package com.vaxcare.unifiedhub.jobs

import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.vaxjob.model.BaseVaxJob
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 24 hour and active job - Called via FCM when eventType is "com.vaxcare.vaxhub.firebase.SYNC.PRODUCTS" for
 * syncing products and LotInventory from backend
 */
@Singleton
class ProductJob @Inject constructor(
    private val productRepository: ProductRepository,
    analyticsRepository: AnalyticsRepository
) : BaseVaxJob(analyticsRepository) {
    companion object {
        const val LOCATION_JOB_NAME = "ProductJob"
    }

    override suspend fun doWork(parameter: Any?) {
        productRepository.syncProducts(isCalledByJob = true)
    }
}
