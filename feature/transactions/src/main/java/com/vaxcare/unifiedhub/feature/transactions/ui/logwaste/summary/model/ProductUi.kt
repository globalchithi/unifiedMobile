package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

private val SampleProducts = listOf(
    ProductUi(
        id = 0,
        antigen = "DTap",
        prettyName = "(Daptacel)",
        quantity = 4,
        unitPrice = "$53.00 ea.",
        value = "$212.00",
        lotsPreview = "KMTHYNG95 & 1 more",
        valueFloat = 1.1f,
        presentationIcon = DesignSystemR.drawable.ic_presentation_syringe
    ),
    ProductUi(
        id = 1,
        antigen = "Covid-19",
        prettyName = "(Blue, Moderna - Covid Vaccine For Ages 6 yrs to <12 yrs, MDV)",
        quantity = 13,
        unitPrice = "$23.50 ea.",
        value = "$305.50",
        lotsPreview = "ABC123 & 3 more",
        valueFloat = 1.1f,
        presentationIcon = DesignSystemR.drawable.ic_presentation_multi_vial
    ),
    ProductUi(
        id = 2,
        antigen = "Hep A",
        prettyName = "(Havrix)",
        quantity = 1,
        unitPrice = "$53.00 ea.",
        value = "$53.00",
        lotsPreview = "MNYNG93",
        valueFloat = 1.1f,
        presentationIcon = DesignSystemR.drawable.ic_presentation_syringe
    ),
    ProductUi(
        id = 3,
        antigen = "Hep A",
        prettyName = "(Havrix)",
        quantity = 1,
        unitPrice = "$53.00 ea.",
        value = "$53.00",
        lotsPreview = "MNYNG93",
        valueFloat = 1.1f,
        presentationIcon = DesignSystemR.drawable.ic_presentation_syringe
    ),
    ProductUi(
        id = 4,
        antigen = "IPV",
        prettyName = "(IPOL)",
        quantity = 1,
        unitPrice = "$12.00 ea.",
        value = "$12.00",
        lotsPreview = "KMTHYNG97",
        valueFloat = 1.1f,
        presentationIcon = DesignSystemR.drawable.ic_presentation_syringe
    ),
)

@Immutable
data class ProductUi(
    val id: Int,
    val antigen: String,
    val prettyName: String,
    val quantity: Int,
    val unitPrice: String,
    val value: String,
    val lotsPreview: String,
    val valueFloat: Float,
    @DrawableRes val presentationIcon: Int,
) {
    companion object {
        val Sample = SampleProducts
    }
}
