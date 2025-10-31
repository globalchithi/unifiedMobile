package com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vaxcare.unifiedhub.core.model.inventory.ReturnReason
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.R

enum class ReturnReasonUi(
    @StringRes val menuTextRes: Int,
    @StringRes val fullTextRes: Int,
    val stockAware: Boolean,
) {
    EXPIRED(
        menuTextRes = R.string.return_reason_expired,
        fullTextRes = R.string.rt_product_reason_expired,
        stockAware = true,
    ),
    EXCESS_INVENTORY(
        menuTextRes = R.string.return_reason_excess,
        fullTextRes = R.string.rt_product_reason_excess,
        stockAware = true,
    ),
    FRIDGE_OUT_OF_TEMP(
        menuTextRes = R.string.return_reason_fridge_out_of_temp,
        fullTextRes = R.string.rt_product_reason_fridge_out_of_temp,
        stockAware = false,
    ),
    DELIVER_OUT_OF_TEMP(
        menuTextRes = R.string.return_reason_delivered_out_of_temp,
        fullTextRes = R.string.rt_product_reason_delivered_out_of_temp,
        stockAware = false,
    ),
    RECALLED_BY_MANUFACTURER(
        menuTextRes = R.string.return_reason_recalled,
        fullTextRes = R.string.rt_product_reason_recalled,
        stockAware = false,
    ),
    DAMAGED_IN_TRANSIT(
        menuTextRes = R.string.return_reason_damaged,
        fullTextRes = R.string.rt_product_reason_damaged,
        stockAware = false,
    );

    companion object {
        fun fromDomain(domainReason: ReturnReason): ReturnReasonUi =
            when (domainReason) {
                ReturnReason.EXPIRED -> EXPIRED
                ReturnReason.EXCESS_INVENTORY -> EXCESS_INVENTORY
                ReturnReason.FRIDGE_OUT_OF_TEMP -> FRIDGE_OUT_OF_TEMP
                ReturnReason.DELIVER_OUT_OF_TEMP -> DELIVER_OUT_OF_TEMP
                ReturnReason.RECALLED_BY_MANUFACTURER -> RECALLED_BY_MANUFACTURER
                ReturnReason.DAMAGED_IN_TRANSIT -> DAMAGED_IN_TRANSIT
            }
    }

    @Composable
    fun getFullText(stock: StockUi): String =
        if (stockAware) {
            stringResource(
                id = R.string.returns_stock_aware_reason,
                stringResource(fullTextRes),
                stock.prettyName
            )
        } else {
            stringResource(fullTextRes)
        }

    fun toDomain(): ReturnReason =
        when (this) {
            EXPIRED -> ReturnReason.EXPIRED
            EXCESS_INVENTORY -> ReturnReason.EXCESS_INVENTORY
            FRIDGE_OUT_OF_TEMP -> ReturnReason.FRIDGE_OUT_OF_TEMP
            DELIVER_OUT_OF_TEMP -> ReturnReason.DELIVER_OUT_OF_TEMP
            RECALLED_BY_MANUFACTURER -> ReturnReason.RECALLED_BY_MANUFACTURER
            DAMAGED_IN_TRANSIT -> ReturnReason.DAMAGED_IN_TRANSIT
        }
}
