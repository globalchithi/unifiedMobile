package com.vaxcare.unifiedhub.feature.home.ui.onhand.model

import androidx.annotation.DrawableRes

data class ProductUI(
    val productId: Int,
    val antigen: String,
    val prettyName: String,
    val onHand: Int,
    val isExpired: Boolean,
    @DrawableRes val presentationIcon: Int,
)

data class GroupedProducts(
    val inventoryGroup: String,
    val antigen: String,
    val products: List<ProductUI>,
    val onHand: Int
)
