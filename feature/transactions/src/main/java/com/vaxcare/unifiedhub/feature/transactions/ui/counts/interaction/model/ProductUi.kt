package com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model

import com.vaxcare.unifiedhub.core.model.product.Presentation

data class ProductUi(
    val id: Int = 0,
    val antigen: String = "",
    val prettyName: String = "",
    val cartonCount: Int = 0,
    val presentation: Presentation = Presentation.UNKNOWN
)
