package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.data.mapper.LotNumberMapper
import com.vaxcare.unifiedhub.core.database.dao.LotNumberDao
import com.vaxcare.unifiedhub.core.database.model.inventory.lot.LotNumberEntity
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.lot.LotNumberSource
import com.vaxcare.unifiedhub.core.network.api.InventoryApi
import com.vaxcare.unifiedhub.core.network.model.LotNumberDTO
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

interface LotRepository {
    fun getAllLots(): Flow<List<Lot>>

    fun getAllLotsByProductId(productId: Int): Flow<List<Lot>>

    fun getExpiredLots(): Flow<List<Lot>>

    fun getLotByNumber(number: String): Flow<Lot?>

    fun getLotsExpiringBefore(date: LocalDate): Flow<List<Lot>>

    suspend fun getLotByNumberAsync(number: String): Lot?

    suspend fun getLotsByNumber(numbers: List<String>): List<Lot>

    suspend fun getProductIdByLotNumber(number: String?): Int?

    suspend fun getProductIdsByLotNumber(numbers: List<String>): List<Int>

    suspend fun postLot(
        expirationDate: LocalDate,
        lotNumber: String,
        productId: Int,
        source: Int,
        unreviewed: Boolean = false,
        isCalledByJob: Boolean = false,
    )

    suspend fun insertTemporaryLot(
        expirationDate: LocalDate,
        lotNumber: String,
        productId: Int,
        source: Int,
    )

    suspend fun insertLots(lots: List<LotNumberDTO>)

    suspend fun syncLots(isCalledByJob: Boolean)
}

private const val CUT_OFF_DAYS = 365

class LotRepositoryImpl @Inject constructor(
    private val inventoryApi: InventoryApi,
    private val lotNumberDao: LotNumberDao,
    private val lotNumberMapper: LotNumberMapper,
) : LotRepository {
    override fun getAllLots(): Flow<List<Lot>> =
        lotNumberDao
            .getAllLots()
            .mapNotNull(lotNumberMapper::entityToDomain)

    override fun getAllLotsByProductId(productId: Int): Flow<List<Lot>> =
        lotNumberDao
            .getLotsByProductId(productId)
            .mapNotNull(lotNumberMapper::entityToDomain)

    override fun getExpiredLots(): Flow<List<Lot>> =
        lotNumberDao
            .getLotsExpiringBefore(LocalDate.now())
            .map(lotNumberMapper::entityToDomain)

    override fun getLotsExpiringBefore(date: LocalDate): Flow<List<Lot>> =
        lotNumberDao
            .getLotsExpiringBefore(date)
            .map(lotNumberMapper::entityToDomain)

    override fun getLotByNumber(number: String): Flow<Lot?> =
        lotNumberDao
            .getLotByNumber(number)
            .map {
                lotNumberMapper.entityToDomain(it)
            }

    override suspend fun getLotByNumberAsync(number: String): Lot? =
        lotNumberDao
            .getLotByNumber(number)
            .first()
            ?.let(lotNumberMapper::entityToDomain)

    override suspend fun getLotsByNumber(numbers: List<String>): List<Lot> =
        lotNumberDao
            .getLotsByNumber(numbers)
            .mapNotNull(lotNumberMapper::entityToDomain)

    override suspend fun getProductIdByLotNumber(number: String?): Int? {
        if (number.isNullOrEmpty()) return null
        return lotNumberDao.getProductIdByLotNumber(number)
    }

    override suspend fun getProductIdsByLotNumber(numbers: List<String>): List<Int> =
        numbers.mapNotNull {
            getProductIdByLotNumber(it)
        }

    override suspend fun insertTemporaryLot(
        expirationDate: LocalDate,
        lotNumber: String,
        productId: Int,
        source: Int,
    ) {
        val localLotNumber = LotNumberEntity(
            expirationDate = expirationDate,
            id = -1,
            lotNumber = lotNumber,
            productId = productId,
            salesLotNumberId = -1,
            salesProductId = -1,
            source = source,
            unreviewed = source != LotNumberSource.VaxHubScan.id
        )

        lotNumberDao.insertAll(listOf(localLotNumber))
    }

    override suspend fun postLot(
        expirationDate: LocalDate,
        lotNumber: String,
        productId: Int,
        source: Int,
        unreviewed: Boolean,
        isCalledByJob: Boolean
    ) {
        val localLotNumber = LotNumberDTO(
            expirationDate = expirationDate,
            id = -1, // This id doesn't matter as it will be replaced by the backend
            qualifiedLotNumber = lotNumber,
            epProductId = productId,
            salesLotNumberId = -1,
            salesProductId = -1,
            source = source,
            unreviewed = unreviewed
        )
        val lotNumbers = inventoryApi
            .postLotNumber(localLotNumber, isCalledByJob)
            .onEach { it.expirationDate = it.expirationDate?.plusDays(1) }
        lotNumberDao.insertAll(lotNumberMapper.networkToEntity(lotNumbers))
    }

    override suspend fun insertLots(lots: List<LotNumberDTO>) {
        lotNumberDao.insertAll(lotNumberMapper.networkToEntity(lots))
    }

    override suspend fun syncLots(isCalledByJob: Boolean) {
        try {
            val lotNumbers = inventoryApi
                .getLotNumbers(
                    isCalledByJob = true,
                    expiredCutoffDays = CUT_OFF_DAYS
                ).onEach {
                    it.expirationDate = it.expirationDate?.plusDays(1)
                }
            lotNumberDao.deleteAll()
            lotNumberDao.insertAll(lotNumberMapper.networkToEntity(lotNumbers))
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e(e, "Exception getting LotNumbers: ${e.message}")
        }
    }
}
