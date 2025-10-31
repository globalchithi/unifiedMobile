package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.data.model.inventory.enums.TransactionType
import com.vaxcare.unifiedhub.core.database.model.enums.InventorySource
import com.vaxcare.unifiedhub.core.database.model.inventory.count.CountEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.count.CountEntryEntity
import com.vaxcare.unifiedhub.core.model.Count
import com.vaxcare.unifiedhub.core.model.inventory.LotInventory
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.network.model.CountEntryRequestDTO
import com.vaxcare.unifiedhub.core.network.model.CountEntryResponseDTO
import com.vaxcare.unifiedhub.core.network.model.CountRequestDTO
import com.vaxcare.unifiedhub.core.network.model.CountResponseDTO
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class CountMapper @Inject constructor() {
    fun networkToEntity(data: List<CountResponseDTO>) =
        data.map {
            CountEntity(
                clinicId = it.clinicId,
                createdOn = it.createdOn,
                guid = it.guid,
                stock = InventorySource.fromId(it.stock),
                userId = it.userId
            )
        }

    fun entityToNetwork(entries: List<CountEntryEntity>, counts: List<CountEntity>): List<CountResponseDTO> {
        val countKeys = counts.associateBy { it.guid }
        val entryKeys = entries.map { it.countGuid to it }
        return (countKeys.keys + entryKeys.map { it.first }).mapNotNull { countGuid ->
            val foundCountEntity = countKeys[countGuid]
            val foundEntryEntities = entryKeys.filter { it.first == countGuid }
            if (foundCountEntity == null) {
                null
            } else {
                CountResponseDTO(
                    clinicId = foundCountEntity.clinicId,
                    createdOn = foundCountEntity.createdOn,
                    guid = countGuid,
                    stock = foundCountEntity.stock.id,
                    userId = foundCountEntity.userId,
                    countList = foundEntryEntities.map { (_, entry) ->
                        CountEntryResponseDTO(
                            countGuid = entry.countGuid,
                            doseValue = entry.doseValue,
                            epProductId = entry.epProductId,
                            guid = entry.guid,
                            lotNumber = entry.lotNumber,
                            newOnHand = entry.newOnHand,
                            prevOnHand = entry.prevOnHand
                        )
                    }
                )
            }
        }
    }

    fun domainToNetwork(
        count: Count,
        confirmedProductIds: List<Int>,
        lotInventory: Map<String, LotInventory>,
        lots: Map<String, Lot>,
        products: Map<Int, Product>,
        userId: Int,
        userName: String,
        clinicId: Long,
    ): CountRequestDTO {
        val zeroDeltaLotNumbers = lots
            .filter {
                confirmedProductIds.contains(it.value.productId) && count.lotEntries[it.key]?.first == null
            }.keys

        val finalLotEntries = count.lotEntries
            .mapValues { (lotNumber, state) ->
                if (zeroDeltaLotNumbers.contains(lotNumber)) {
                    0 to state.second
                } else {
                    state
                }
            }.filter { it.value.first != null }

        val entriesToSubmit = finalLotEntries
            .mapNotNull { (lotNumber, p) ->
                val delta = p.first!!
                val receiptKey = p.second

                // nulls are okay here, as added/scanned lots will have null inventory
                val inventory = lotInventory[lotNumber]

                val lot = lots[lotNumber]
                if (lot == null) {
                    Timber.e("No lot found for lot number $lotNumber when attempting to submit the count.")
                    return@mapNotNull null
                }

                val product = products[lot.productId]
                if (product == null) {
                    Timber.e("No product found for lot number $lotNumber when attempting to submit the count.")
                    return@mapNotNull null
                }

                val onHand = inventory?.onHand ?: 0
                CountEntryRequestDTO(
                    countGuid = count.transactionKey,
                    groupGuid = count.groupGuid,
                    entryId = receiptKey,
                    productId = lot.salesProductId,
                    epProductId = lot.productId,
                    newOnHand = onHand + delta,
                    prevOnHand = onHand,
                    // The backend expects a "real" delta, e.g. a positive change should be a negative
                    // delta
                    delta = -delta,
                    doseValue = if (count.stock == StockType.PRIVATE) product.lossFee ?: 0F else 0F,
                    lotNumber = lotNumber,
                    lotExpirationDate = lots[lotNumber]?.expiration ?: LocalDate.of(1970, 1, 1),
                    stock = count.stock.id.toString(),
                    adjustmentType = TransactionType.COUNT.id.toString(),
                    userId = userId,
                    userName = userName,
                )
            }

        return CountRequestDTO(
            guid = count.countGuid,
            clinicId = clinicId,
            userId = userId,
            stock = count.stock.id.toString(),
            createdOn = LocalDateTime.now(),
            vaccineCountEntries = entriesToSubmit
        )
    }
}
