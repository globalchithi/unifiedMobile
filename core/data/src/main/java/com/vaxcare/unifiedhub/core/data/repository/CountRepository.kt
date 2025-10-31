package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.mapper.CountEntryMapper
import com.vaxcare.unifiedhub.core.data.mapper.CountMapper
import com.vaxcare.unifiedhub.core.database.dao.CountDao
import com.vaxcare.unifiedhub.core.database.model.enums.InventorySource
import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import com.vaxcare.unifiedhub.core.model.Count
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.network.api.InventoryApi
import kotlinx.coroutines.async
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class CountRepository @Inject constructor(
    private val inventoryApi: InventoryApi,
    private val countDao: CountDao,
    private val countMapper: CountMapper,
    private val countEntryMapper: CountEntryMapper,
    private val lotRepository: LotRepository,
    private val postDtoFactory: PostDtoFactory,
    private val productRepository: ProductRepository,
    private val inventoryRepository: LotInventoryRepository,
    private val locationPrefs: LocationPreferenceDataSource,
    private val userSessionPrefs: UserSessionPreferenceDataSource,
    private val dispatcherProvider: DispatcherProvider,
) {
    fun getLatestCountDate(stockType: StockType): Flow<LocalDateTime?> =
        countDao.getLatestCountDate(InventorySource.fromId(stockType.id))

    suspend fun refreshCounts(isCalledByJob: Boolean) {
        try {
            val reqBody = postDtoFactory.createPost(listOf<String>())
            inventoryApi.getVaccineCount(reqBody, isCalledByJob)?.let { response ->
                val entries = countEntryMapper.networkToEntity(response)
                val counts = countMapper.networkToEntity(response)

                with(countDao) {
                    deleteCounts()
                    deleteCountEntries()
                    insertCounts(counts)
                    insertCountEntries(entries)
                }
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e(e, "Exception refreshing counts: ${e.message}")
        }
    }

    suspend fun submitCount(count: Count, confirmedIds: List<Int>): Boolean {
        val payload = withContext(dispatcherProvider.io) {
            val inventory = inventoryRepository
                .getLotInventory(count.stock)
                .first()
                .associateBy { it.lotNumber }
            val lotNumbers = (inventory.keys + count.lotEntries.keys).toList()

            val lotsByNumberDeferred = async {
                lotRepository.getLotsByNumber(lotNumbers)
            }
            val productsDeferred = async {
                productRepository
                    .getProductsByLotNumber(lotNumbers)
                    .associateBy { it.id }
            }
            val lots = lotsByNumberDeferred
                .await()
                .map {
                    it.apply {
                        if (salesProductId == -1) {
                            salesProductId = productRepository
                                .getSalesProductIdFromProductId(it.productId) ?: productId
                        }
                    }
                }.associateBy { it.lotNumber }

            countMapper.domainToNetwork(
                count = count,
                confirmedProductIds = confirmedIds,
                lotInventory = inventory,
                lots = lots,
                products = productsDeferred.await(),
                userId = userSessionPrefs.userId.first().toInt(),
                userName = userSessionPrefs.userName.first(),
                clinicId = locationPrefs.parentClinicId.first()
            )
        }

        try {
            inventoryApi.postCount(postDtoFactory.createPost(payload))
            return true
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Encountered an exception while submitting the count:\n$e")
            return false
        }
    }
}
