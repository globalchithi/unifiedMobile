package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model

data class AddedLotInventoryUi(
    val lotNumber: String,
    val expiration: String,
    val count: Int,
    val isExpired: Boolean = false,
    val isDeleted: Boolean = false,
)
