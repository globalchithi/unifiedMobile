package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.model

import androidx.compose.runtime.Immutable

@Immutable
data class LotInventoryUi(
    val lotNumber: String,
    val quantity: Int,
    val isDeleted: Boolean
)
