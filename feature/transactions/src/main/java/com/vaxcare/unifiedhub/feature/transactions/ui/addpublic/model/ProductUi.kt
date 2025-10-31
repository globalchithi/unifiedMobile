package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.pluralStringResource
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.feature.transactions.R

private val SampleProducts = listOf(
    ProductUi(
        id = 0,
        antigen = "DTap",
        prettyName = "Daptacel",
        inventory = listOf(
            AddedLotInventoryUi(
                lotNumber = "VXC7716",
                count = 9,
                expiration = "06/06/26"
            )
        ),
        cartonCount = 20,
        presentation = Presentation.PREFILLED_SYRINGE
    ),
    ProductUi(
        id = 1,
        antigen = "DTap + IPV + HIB",
        prettyName = "Very long pretty name for some reason",
        inventory = listOf(
            AddedLotInventoryUi(
                lotNumber = "VXC8712",
                count = 12,
                expiration = "09/01/26"
            )
        ),
        cartonCount = 20,
        presentation = Presentation.NASAL_SYRINGE
    ),
    ProductUi(
        id = 2,
        antigen = "Hep A",
        prettyName = "Havrix",
        inventory = listOf(
            AddedLotInventoryUi(
                lotNumber = "VXC8712",
                count = 3,
                expiration = "12/02/25"
            )
        ),
        cartonCount = 20,
        presentation = Presentation.MULTI_DOSE_VIAL
    ),
)

@Immutable
data class ProductUi(
    val id: Int,
    val inventory: List<AddedLotInventoryUi> = emptyList(),
    val cartonCount: Int,
    val antigen: String,
    val prettyName: String,
    val isDeleted: Boolean = false,
    val presentation: Presentation
) {
    companion object {
        val Sample = SampleProducts
    }

    fun getTotal(): Int =
        inventory
            .filter { !it.isDeleted }
            .sumOf { it.count }

    @Composable
    fun getLotDetails(): String =
        pluralStringResource(
            id = R.plurals.lot_number_detail_text,
            count = inventory.size,
            inventory.first().lotNumber,
            inventory.size - 1
        )
}
