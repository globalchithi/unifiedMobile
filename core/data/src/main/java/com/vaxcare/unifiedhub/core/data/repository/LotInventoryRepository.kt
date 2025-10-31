package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.mapper.AdjustmentMapper
import com.vaxcare.unifiedhub.core.data.mapper.LotInventoryMapper
import com.vaxcare.unifiedhub.core.database.dao.LotInventoryDao
import com.vaxcare.unifiedhub.core.database.dao.ProductDao
import com.vaxcare.unifiedhub.core.database.model.enums.ProductStatus
import com.vaxcare.unifiedhub.core.model.inventory.Adjustment
import com.vaxcare.unifiedhub.core.model.inventory.LotInventory
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.network.api.InventoryApi
import com.vaxcare.unifiedhub.core.network.model.LotInventoryDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface LotInventoryRepository {
    suspend fun syncLotInventory(isCalledByJob: Boolean = false)

    fun getLotInventory(stockType: StockType): Flow<List<LotInventory>>

    fun getLotInventory(lotNumbers: List<String>, stockType: StockType): Flow<List<LotInventory>>

    suspend fun getLotInventoryAsync(lotNumber: String, stockType: StockType): LotInventory?

    fun getLotInventoryByProductAndSourceId(productId: Int?, sourceId: Int): Flow<List<LotInventory>>

    fun getLotInventoryTotalValue(stockType: StockType): Float

    suspend fun postInventoryAdjustments(adjustments: Adjustment)
}

class LotInventoryRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val lotInventoryDao: LotInventoryDao,
    private val inventoryApi: InventoryApi,
    private val lotInventoryMapper: LotInventoryMapper,
    private val dispatcherProvider: DispatcherProvider,
    private val postDtoFactory: PostDtoFactory,
    private val adjustmentMapper: AdjustmentMapper
) : LotInventoryRepository {
    override suspend fun syncLotInventory(isCalledByJob: Boolean) {
        withContext(dispatcherProvider.io) {
            val result = inventoryApi.getLotInventory(isCalledByJob)
            insertLotInventoryWithEnabledProduct(result.inventory)
        }
    }

    override fun getLotInventory(stockType: StockType): Flow<List<LotInventory>> =
        lotInventoryDao
            .getLotInventoryByStock(stockType.id)
            .flowOn(dispatcherProvider.io)
            .map(lotInventoryMapper::entityToDomain)

    override fun getLotInventory(lotNumbers: List<String>, stockType: StockType): Flow<List<LotInventory>> =
        lotInventoryDao
            .getLotInventory(lotNumbers, stockType.id)
            .map(lotInventoryMapper::entityToDomain)

    override suspend fun getLotInventoryAsync(lotNumber: String, stockType: StockType): LotInventory? =
        lotInventoryDao
            .getLotInventory(lotNumber, stockType.id)
            .map { it?.let { lotInventoryMapper.entityToDomain(it) } }
            .firstOrNull()

    override fun getLotInventoryByProductAndSourceId(productId: Int?, sourceId: Int): Flow<List<LotInventory>> {
        if (productId == null) return flowOf(emptyList())
        return lotInventoryDao
            .getLotInventoryByProductIdAndSourceId(productId, sourceId)
            .map { lotInventoryMapper.entityToDomain(it) }
    }

    override fun getLotInventoryTotalValue(stockType: StockType): Float =
        lotInventoryDao.getLotInventoryTotalValue(stockType.id)

    override suspend fun postInventoryAdjustments(adjustments: Adjustment) {
        val adjustmentItems = adjustmentMapper.domainToNetwork(adjustments)
        val body = postDtoFactory.createPost(adjustmentItems)
        inventoryApi.postAdjustments(body)
    }

    private suspend fun insertLotInventoryWithEnabledProduct(lotInventory: List<LotInventoryDTO>) {
        val enabledProds = productDao
            .getAllProductsAsync()
            .filter { product ->
                product.status in listOf(ProductStatus.VACCINE_ENABLED, ProductStatus.FLU_ENABLED)
            }.map { product -> product.id }
        lotInventory.forEach {
            it.productStatus = if (it.productId in enabledProds) {
                1
            } else {
                0
            }
        }

        with(lotInventoryDao) {
            deleteAll()
            insertLotInventory(lotInventoryMapper.networkToEntity(lotInventory))
        }
    }
}
