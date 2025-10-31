package com.vaxcare.unifiedhub.feature.transactions.ui.returns.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import com.vaxcare.unifiedhub.core.common.ext.toLocalDateString
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.feature.transactions.R
import java.time.LocalDate

private val expiredDate = LocalDate
    .now()
    .minusYears(1)
    .toLocalDateString("MM/dd/yyyy")
private val unexpiredDate = LocalDate
    .now()
    .plusYears(1)
    .toLocalDateString("MM/yyyy")

val SampleData = listOf(
    ProductLotUi(
        antigen = "Antigen 1",
        prettyName = "Pretty Name 1",
        quantity = 5,
        lotNumber = "VXC1234",
        expiration = expiredDate,
        presentation = Presentation.PREFILLED_SYRINGE,
        isExpired = true
    ),
    ProductLotUi(
        antigen = "Antigen 2",
        prettyName = "Pretty Name 2",
        quantity = 10,
        lotNumber = "VXC5678",
        expiration = expiredDate,
        presentation = Presentation.NASAL_SPRAY,
        isExpired = true
    ),
    ProductLotUi(
        antigen = "Antigen 3",
        prettyName = "Pretty Name 3",
        quantity = 2,
        lotNumber = "VXC9999",
        expiration = unexpiredDate,
        presentation = Presentation.SINGLE_DOSE_TUBE,
        isExpired = false,
        isDeleted = true
    ),
)

data class ProductLotUi(
    val antigen: String,
    val prettyName: String,
    val quantity: Int,
    val lotNumber: String,
    val expiration: String,
    val presentation: Presentation,
    val isExpired: Boolean = false,
    val isDeleted: Boolean = false,
) {
    companion object {
        val Sample = SampleData
    }

    @Composable
    fun getLotInfo(): String =
        pluralStringResource(
            id = R.plurals.lot_number_detail_text,
            count = 1,
            lotNumber,
        )
}
