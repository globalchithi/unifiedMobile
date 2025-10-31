package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.util.fastSumBy
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.ProductInfoUi
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.SubtitleLine
import com.vaxcare.unifiedhub.feature.transactions.R

private val SampleProducts = listOf(
    ProductUi(
        productId = 0,
        isDeleted = false,
        antigen = "DTap",
        prettyName = "Daptacel",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC7716",
                quantity = 1,
                isDeleted = false
            ),
            LotInventoryUi(
                lotNumber = "Lot1",
                quantity = 1,
                isDeleted = false
            ),
            LotInventoryUi(
                lotNumber = "Lot2",
                quantity = 1,
                isDeleted = false
            ),
        ),
        presentation = Presentation.PREFILLED_SYRINGE,
    ),
    ProductUi(
        productId = 2,
        isDeleted = true,
        antigen = "Hep A",
        prettyName = "Havrix",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC8712",
                quantity = 12,
                isDeleted = false
            )
        ),
        presentation = Presentation.PREFILLED_SYRINGE,
    ),
    ProductUi(
        productId = 3,
        isDeleted = false,
        antigen = "Hep B",
        prettyName = "DesignSystemR HB",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC8712",
                quantity = 12,
                isDeleted = false
            )
        ),
        presentation = Presentation.PREFILLED_SYRINGE,
    ),
    ProductUi(
        productId = 4,
        isDeleted = false,
        antigen = "HPV",
        prettyName = "Gardasil",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC8242",
                quantity = 12,
                isDeleted = false
            )
        ),
        presentation = Presentation.PREFILLED_SYRINGE,
    ),
    ProductUi(
        productId = 5,
        isDeleted = false,
        antigen = "INFLUE",
        prettyName = "Something",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC2342",
                quantity = 10,
                isDeleted = false
            )
        ),
        presentation = Presentation.PREFILLED_SYRINGE,
    ),
    ProductUi(
        productId = 6,
        isDeleted = false,
        antigen = "SAMPLE",
        prettyName = "Sample",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC9999",
                quantity = 10,
                isDeleted = false
            )
        ),
        presentation = Presentation.PREFILLED_SYRINGE,
    ),
    ProductUi(
        productId = 7,
        isDeleted = false,
        antigen = "SAMPLE",
        prettyName = "Sample",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC9999",
                quantity = 10,
                isDeleted = false
            )
        ),
        presentation = Presentation.PREFILLED_SYRINGE,
    ),
    ProductUi(
        productId = 1,
        isDeleted = true,
        antigen = "DTap + IPV + HIB",
        prettyName = "Very long pretty name for some reason",
        inventory = listOf(
            LotInventoryUi(
                lotNumber = "VXC8712",
                quantity = 12,
                isDeleted = false
            )
        ),
        presentation = Presentation.PREFILLED_SYRINGE,
    ),
)

data class ProductUi(
    val productId: Int,
    val isDeleted: Boolean,
    val inventory: List<LotInventoryUi>,
    val antigen: String,
    val prettyName: String,
    val presentation: Presentation,
) {
    companion object {
        val Sample = SampleProducts
    }

    fun getQuantity(): Int = inventory.filter { !it.isDeleted }.fastSumBy { it.quantity }

    private fun getProductName(): String = "($prettyName)"

    @Composable
    fun getLotInfo(): String =
        pluralStringResource(
            R.plurals.lot_number_detail_text,
            inventory.size,
            inventory.first().lotNumber,
            inventory.size - 1
        )

    // using @Composable here to access stringResource. The power of layer-scoped data models!
    @Composable
    fun getLotDetails(): List<SubtitleLine> =
        buildList {
            add(
                SubtitleLine(
                    text = pluralStringResource(
                        R.plurals.lot_number_detail_text,
                        inventory.size,
                        inventory.first().lotNumber,
                        inventory.size - 1
                    )
                )
            )
        }

    @Composable
    fun toProductInfoUi(): ProductInfoUi =
        ProductInfoUi(
            mainTextBold = antigen,
            mainTextRegular = getProductName(),
            presentation = presentation,
            subtitleLines = getLotDetails(),
            isDeleted = isDeleted,
        )
}
