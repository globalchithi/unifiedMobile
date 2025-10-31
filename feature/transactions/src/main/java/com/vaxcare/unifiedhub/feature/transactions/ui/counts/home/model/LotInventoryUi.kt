package com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.model

import androidx.compose.runtime.Immutable

@Immutable
data class LotInventoryUi(
    val lotNumber: String,
    val initialQuantity: Int,
    val delta: Int?,
)
