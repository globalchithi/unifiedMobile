package com.vaxcare.unifiedhub.core.ui

import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.model.product.Presentation.IMPLANT
import com.vaxcare.unifiedhub.core.model.product.Presentation.IUD
import com.vaxcare.unifiedhub.core.model.product.Presentation.MULTI_DOSE_VIAL
import com.vaxcare.unifiedhub.core.model.product.Presentation.NASAL_SPRAY
import com.vaxcare.unifiedhub.core.model.product.Presentation.NASAL_SYRINGE
import com.vaxcare.unifiedhub.core.model.product.Presentation.PREFILLED_SYRINGE
import com.vaxcare.unifiedhub.core.model.product.Presentation.SINGLE_DOSE_TUBE
import com.vaxcare.unifiedhub.core.model.product.Presentation.SINGLE_DOSE_VIAL
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

object Icons {
    fun presentationIcon(presentation: Presentation) =
        when (presentation) {
            SINGLE_DOSE_VIAL -> DesignSystemR.drawable.ic_presentation_single_vial
            SINGLE_DOSE_TUBE -> DesignSystemR.drawable.ic_presentation_single_tube
            MULTI_DOSE_VIAL -> DesignSystemR.drawable.ic_presentation_multi_vial
            PREFILLED_SYRINGE -> DesignSystemR.drawable.ic_presentation_syringe
            NASAL_SPRAY -> DesignSystemR.drawable.ic_presentation_nasal
            NASAL_SYRINGE -> DesignSystemR.drawable.ic_presentation_nasal
            IUD -> DesignSystemR.drawable.ic_presentation_iud
            IMPLANT -> DesignSystemR.drawable.ic_presentation_implant
            else -> DesignSystemR.drawable.ic_presentation_syringe
        }
}
