package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.data.mapper.AdjustmentMapper
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import com.vaxcare.unifiedhub.core.model.AdjustmentType
import com.vaxcare.unifiedhub.core.model.inventory.Adjustment
import com.vaxcare.unifiedhub.core.model.inventory.AdjustmentEntry
import com.vaxcare.unifiedhub.core.model.inventory.AdjustmentReason
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.network.api.InventoryApi
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class AdjustmentRepository @Inject constructor(
    private val inventoryApi: InventoryApi,
    private val adjustmentMapper: AdjustmentMapper,
    private val postDtoFactory: PostDtoFactory,
    private val userSessionPrefs: UserSessionPreferenceDataSource,
) {
    private var cachedAdjustment: Adjustment? = null

    suspend fun buildAndSubmitAdjustment(
        key: String,
        groupGuid: String,
        stock: StockType,
        type: AdjustmentType,
        reason: AdjustmentReason? = null,
        entries: List<AdjustmentEntry>,
    ): Boolean {
        // empty entries is an invalid adjustment and should not be submitted
        if (entries.isEmpty()) return false

        cachedAdjustment = Adjustment(
            key = key,
            groupGuid = groupGuid,
            adjustmentReasonType = reason?.getReasonString(),
            type = type,
            adjustments = entries,
            stockId = stock.id.toString(),
            userId = userSessionPrefs.userId.first(),
            userName = userSessionPrefs.userName.first()
        )

        return submitCachedAdjustment()
    }

    suspend fun submitCachedAdjustment(): Boolean {
        adjustmentMapper
            .domainToNetwork(cachedAdjustment ?: return false)
            .let {
                try {
                    inventoryApi.postAdjustments(postDtoFactory.createPost(it))
                    cachedAdjustment = null
                    return true
                } catch (e: Exception) {
                    coroutineContext.ensureActive()
                    Timber.e("Encountered an exception while submitting the adjustment.")
                    return false
                }
            }
    }
}
