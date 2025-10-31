package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.data.mapper.ReturnMapper
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import com.vaxcare.unifiedhub.core.model.PickupAvailability
import com.vaxcare.unifiedhub.core.model.inventory.Return
import com.vaxcare.unifiedhub.core.model.inventory.ReturnReason
import com.vaxcare.unifiedhub.core.model.inventory.ReturnedLot
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.network.api.InventoryApi
import com.vaxcare.unifiedhub.core.network.api.ReturnApi
import com.vaxcare.unifiedhub.core.network.model.PickupAvailabilityResponseDto
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

interface ReturnRepository {
    suspend fun getPickupAvailability(): List<PickupAvailability>

    suspend fun buildAndSubmitReturn(
        groupGuid: String,
        stock: StockType,
        reason: ReturnReason,
        noOfLabels: Int?,
        pickup: PickupAvailability?,
        returnedLots: List<ReturnedLot>
    ): Boolean

    suspend fun submitCachedReturn(): Boolean
}

class ReturnRepositoryImpl @Inject constructor(
    private val returnApi: ReturnApi,
    private val inventoryApi: InventoryApi,
    private val returnMapper: ReturnMapper,
    private val userSessionPrefs: UserSessionPreferenceDataSource,
) : ReturnRepository {
    companion object {
        private val PICKUP_AVAILABILITY_REGEX =
            "(\\d{1,2}/\\d{1,2}/\\d{4}) (\\d{1,2}:\\d{2}[AP]M)-(\\d{1,2}:\\d{2}[AP]M)".toRegex()

        private val INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH)
        private val INPUT_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH)
    }

    private var cachedReturn: Return? = null

    override suspend fun getPickupAvailability(): List<PickupAvailability> =
        returnApi.getPickUpAvailability().mapNotNull {
            it.toDomain()
        }

    override suspend fun buildAndSubmitReturn(
        groupGuid: String,
        stock: StockType,
        reason: ReturnReason,
        noOfLabels: Int?,
        pickup: PickupAvailability?,
        returnedLots: List<ReturnedLot>
    ): Boolean {
        // empty lots is an invalid return and should not be submitted
        if (returnedLots.isEmpty()) return false

        cachedReturn = Return(
            groupGuid = groupGuid,
            stockId = stock.id,
            reasonId = reason.getReasonId(),
            pickup = pickup,
            noOfLabels = noOfLabels,
            returnedLots = returnedLots,
            userId = userSessionPrefs.userId.first(),
            userName = userSessionPrefs.userName.first()
        )

        return submitCachedReturn()
    }

    override suspend fun submitCachedReturn(): Boolean {
        returnMapper
            .domainToNetwork(cachedReturn ?: return false)
            .let {
                if (it == null) return false

                try {
                    inventoryApi.postReturn(it)
                    cachedReturn = null
                    return true
                } catch (e: Exception) {
                    coroutineContext.ensureActive()
                    Timber.e("Encountered an exception while submitting the return: $e")
                    return false
                }
            }
    }

    private fun ReturnReason.getReasonId(): Int =
        when (this) {
            ReturnReason.EXPIRED -> 1
            ReturnReason.DELIVER_OUT_OF_TEMP -> 2
            ReturnReason.RECALLED_BY_MANUFACTURER -> 3
            ReturnReason.FRIDGE_OUT_OF_TEMP -> 6
            ReturnReason.DAMAGED_IN_TRANSIT -> 7
            ReturnReason.EXCESS_INVENTORY -> 12
        }

    private fun PickupAvailabilityResponseDto.toDomain(): PickupAvailability? {
        val matchResult = PICKUP_AVAILABILITY_REGEX.find(this.text)

        if (matchResult != null) {
            val (dateStr, startTimeStr, endTimeStr) = matchResult.destructured

            return PickupAvailability(
                date = LocalDate.parse(dateStr, INPUT_DATE_FORMATTER),
                startTime = LocalTime.parse(startTimeStr, INPUT_TIME_FORMATTER).withSecond(0),
                endTime = LocalTime.parse(endTimeStr, INPUT_TIME_FORMATTER).withSecond(0)
            )
        }

        Timber.e("Could not parse pickup time: ${this.text}")
        return null
    }
}
