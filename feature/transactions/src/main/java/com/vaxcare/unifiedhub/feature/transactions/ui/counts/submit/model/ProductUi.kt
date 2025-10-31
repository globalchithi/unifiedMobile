package com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.model

import androidx.annotation.DrawableRes

data class ProductUi(
    val id: Int,
    val antigen: String,
    val prettyName: String,
    val quantity: Int,
    val unitPrice: String,
    val delta: Int,
    val impact: String,
    @DrawableRes val presentationIcon: Int,
)
