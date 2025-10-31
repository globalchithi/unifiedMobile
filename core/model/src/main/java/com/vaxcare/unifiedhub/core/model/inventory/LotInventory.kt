package com.vaxcare.unifiedhub.core.model.inventory

data class LotInventory(
    val lotNumber: String,
    val onHand: Int,
    val inventorySourceId: Int,
    val delta: Int? = null,
    val isDeleted: Boolean = false,
    val inventoryGroup: String? = null,
    val antigen: String? = null,
    val productId: Int = -1
)
