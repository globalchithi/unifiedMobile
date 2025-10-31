package com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastSumBy
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

private val SampleProducts = listOf(
    ProductUi(
        productId = 0,
        isConfirmed = false,
        antigen = "DTap",
        prettyName = "Daptacel",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC7716",
                initialQuantity = -9,
                delta = null,
            )
        ),
        cartonCount = 20,
        presentationIcon = DesignSystemR.drawable.ic_presentation_syringe,
    ),
    ProductUi(
        productId = 1,
        isConfirmed = true,
        antigen = "DTap + IPV + HIB",
        prettyName = "Very long pretty name for some reason",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC8712",
                initialQuantity = 12,
                delta = -2,
            )
        ),
        cartonCount = 20,
        presentationIcon = DesignSystemR.drawable.ic_presentation_syringe,
    ),
    ProductUi(
        productId = 2,
        isConfirmed = true,
        antigen = "Hep A",
        prettyName = "Havrix",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC8712",
                initialQuantity = 12,
                delta = -2,
            )
        ),
        cartonCount = 20,
        presentationIcon = DesignSystemR.drawable.ic_presentation_syringe,
    ),
    ProductUi(
        productId = 3,
        isConfirmed = false,
        antigen = "Hep B",
        prettyName = "DesignSystemR HB",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC8712",
                initialQuantity = 12,
                delta = -2,
            )
        ),
        cartonCount = 20,
        presentationIcon = DesignSystemR.drawable.ic_presentation_syringe,
    ),
    ProductUi(
        productId = 4,
        isConfirmed = false,
        antigen = "HPV",
        prettyName = "Gardasil",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC8242",
                initialQuantity = 12,
                delta = -2,
            )
        ),
        cartonCount = 20,
        presentationIcon = DesignSystemR.drawable.ic_presentation_syringe,
    ),
    ProductUi(
        productId = 5,
        isConfirmed = false,
        antigen = "INFLUE",
        prettyName = "Something",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC2342",
                initialQuantity = 10,
                delta = 5,
            )
        ),
        cartonCount = 20,
        presentationIcon = DesignSystemR.drawable.ic_presentation_syringe,
    ),
    ProductUi(
        productId = 6,
        isConfirmed = false,
        antigen = "SAMPLE",
        prettyName = "Sample",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC9999",
                initialQuantity = 10,
                delta = 5,
            )
        ),
        cartonCount = 20,
        presentationIcon = DesignSystemR.drawable.ic_presentation_syringe,
    ),
    ProductUi(
        productId = 7,
        isConfirmed = true,
        antigen = "SAMPLE",
        prettyName = "Sample",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC9999",
                initialQuantity = 10,
                delta = 5,
            )
        ),
        cartonCount = 20,
        presentationIcon = DesignSystemR.drawable.ic_presentation_syringe,
    ),
)

@Immutable
data class ProductUi(
    val productId: Int,
    val isConfirmed: Boolean,
    val inventory: List<LotInventoryUi>,
    val antigen: String,
    val prettyName: String,
    val cartonCount: Int,
    @DrawableRes val presentationIcon: Int,
) {
    companion object {
        val Sample = SampleProducts
    }

    fun isActionRequired() =
        inventory.fastAny {
            it.initialQuantity < 0 && (it.delta == null || it.delta == 0)
        }

    fun getQuantities(): Pair<Int, Int?> {
        val initialSum = inventory.fastSumBy { it.initialQuantity }
        val deltaSum = inventory.fastSumBy { it.delta ?: 0 }
        return if (deltaSum != 0) {
            initialSum to initialSum + deltaSum
        } else {
            initialSum to null
        }
    }

    // using @Composable here to access stringResource. The power of layer-scoped data models!
    @Composable
    fun getLotDetails(): String =
        buildString {
            append(
                stringResource(R.string.product_description_carton_size, cartonCount),
                " | ",
                stringResource(R.string.product_lot_lot_number, inventory.first().lotNumber)
            )

            if (inventory.size > 1) {
                append(
                    ' ',
                    stringResource(R.string.product_description_lots_overflow, inventory.size - 1)
                )
            }
        }
}
