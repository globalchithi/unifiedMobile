package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.database.model.enums.InventorySource
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import javax.inject.Inject

class StockTypeMapper @Inject constructor() {
    fun entityToDomain(data: InventorySource): StockType = StockType.valueOf(data.name)
}
