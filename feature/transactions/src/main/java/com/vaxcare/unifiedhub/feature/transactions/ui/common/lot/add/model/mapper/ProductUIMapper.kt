package com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.mapper

import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.ProductUI

internal object ProductUIMapper {
    fun map(product: Product): ProductUI = ProductUI(product.id, product.prettyName ?: product.displayName)
}
