package com.vaxcare.unifiedhub.feature.transactions.ui.returns.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.feature.transactions.R

private val SampleProductData = listOf(
    ProductUi(
        id = 0,
        antigen = "Antigen 1",
        prettyName = "Pretty Name 1",
        quantity = 25,
        lotNumber = "VXC1234",
        noOfLots = 5,
        presentation = Presentation.PREFILLED_SYRINGE,
    ),
    ProductUi(
        id = 1,
        antigen = "Antigen 2",
        prettyName = "Pretty Name 2",
        quantity = 3,
        lotNumber = "VXC2345",
        noOfLots = 1,
        presentation = Presentation.MULTI_DOSE_VIAL,
    ),
    ProductUi(
        id = 2,
        antigen = "Antigen 3",
        prettyName = "Pretty Name 3",
        quantity = 50,
        lotNumber = "VXC3456",
        noOfLots = 10,
        presentation = Presentation.SINGLE_DOSE_TUBE,
    ),
)

data class ProductUi(
    val id: Int,
    val antigen: String,
    val prettyName: String,
    val quantity: Int,
    val lotNumber: String,
    val noOfLots: Int,
    val presentation: Presentation,
) {
    companion object {
        val Sample = SampleProductData
    }

    @Composable
    fun getLotInfo(): String =
        pluralStringResource(
            id = R.plurals.lot_number_detail_text,
            count = noOfLots,
            lotNumber,
            noOfLots - 1
        )
}
