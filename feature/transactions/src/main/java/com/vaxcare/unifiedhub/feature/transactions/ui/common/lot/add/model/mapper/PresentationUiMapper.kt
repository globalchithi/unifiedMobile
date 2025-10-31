package com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.mapper

import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.ui.Icons
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.PresentationUI

object PresentationUiMapper {
    fun map(presentation: Presentation): PresentationUI =
        PresentationUI(
            id = presentation.ordinal,
            name = when (presentation) {
                Presentation.SINGLE_DOSE_VIAL -> R.string.add_new_lot_single_dose_vial
                Presentation.SINGLE_DOSE_TUBE -> R.string.add_new_lot_single_dose_tube
                Presentation.MULTI_DOSE_VIAL -> R.string.add_new_lot_multi_dose_vial
                Presentation.PREFILLED_SYRINGE -> R.string.add_new_lot_prefilled_syringe
                Presentation.NASAL_SPRAY -> R.string.add_new_lot_nasal_spray
                Presentation.NASAL_SYRINGE -> R.string.add_new_lot_nasal_syringe
                Presentation.IUD -> R.string.add_new_lot_iud
                Presentation.IMPLANT -> R.string.add_new_lot_implant
                Presentation.UNKNOWN -> R.string.add_new_lot_unknown
            },
            iconRes = Icons.presentationIcon(presentation),
        )
}
