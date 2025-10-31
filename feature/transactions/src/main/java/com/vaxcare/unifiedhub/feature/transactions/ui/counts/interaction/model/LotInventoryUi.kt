package com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model

data class LotInventoryUi(
    val lotNumber: String,
    val onHand: Int,
    val expiration: String,
    val adjustment: Int? = null,
    val isDeleted: Boolean = false,
    val isExpired: Boolean = false,
    val isActionRequired: Boolean = false
) {
    val delta = adjustment?.let { it - onHand }
}
