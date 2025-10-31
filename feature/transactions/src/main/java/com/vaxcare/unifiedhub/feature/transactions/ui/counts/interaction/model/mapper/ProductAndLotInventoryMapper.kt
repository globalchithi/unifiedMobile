package com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.mapper

import com.vaxcare.unifiedhub.core.model.inventory.LotInventory
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.product.Package
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.LotInventoryUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.ProductUi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

const val EXPIRED_DATE_FORMAT = "MM/dd/yyyy"
const val VANILLA_DATE_FORMAT = "MM/yyyy"

class ProductAndLotInventoryMapper @Inject constructor() {
    fun productDomainToProductInteractionUi(data: Product, pkg: Package) =
        data.let {
            ProductUi(
                antigen = it.antigen,
                id = it.id,
                prettyName = it.prettyName ?: it.displayName,
                cartonCount = pkg.itemCount,
                presentation = data.presentation
            )
        }

    fun lotDomainToLotUi(lotInventory: LotInventory, lot: Lot?): LotInventoryUi? {
        val requireLot = lot ?: return null
        val now = LocalDate.now()
        val expiredDateFmt = DateTimeFormatter.ofPattern(EXPIRED_DATE_FORMAT)
        val vanillaDateFmt = DateTimeFormatter.ofPattern(VANILLA_DATE_FORMAT)

        return lotInventory.let {
            val isExpired = requireLot.expiration?.isBefore(now) ?: false
            val fmt = if (isExpired) {
                expiredDateFmt
            } else {
                vanillaDateFmt
            }
            LotInventoryUi(
                lotNumber = it.lotNumber,
                onHand = it.onHand,
                expiration = requireLot.expiration?.format(fmt) ?: "",
                isExpired = isExpired,
                adjustment = it.delta?.let { delta -> it.onHand + delta },
                isDeleted = it.isDeleted,
                isActionRequired = (it.onHand < 0 && it.delta == null && !it.isDeleted)
            )
        }
    }

    fun lotUiToLotDomain(data: List<LotInventoryUi>, stockId: Int) =
        data.map {
            lotUiToLotDomain(it, stockId)
        }

    fun lotUiToLotDomain(data: LotInventoryUi, stockId: Int) =
        data.let {
            LotInventory(
                lotNumber = it.lotNumber,
                onHand = it.onHand,
                inventorySourceId = stockId,
                delta = it.delta,
                isDeleted = it.isDeleted
            )
        }
}
