package com.vaxcare.unifiedhub.core.model.product

data class Product(
    val id: Int,
    val antigen: String,
    val displayName: String,
    val presentation: Presentation,
    val categoryId: Int,
    val prettyName: String?,
    val lossFee: Float?,
    val inventoryGroup: String,
)
