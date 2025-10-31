package com.vaxcare.unifiedhub.core.model

import com.vaxcare.unifiedhub.core.model.inventory.StockType

data class Count(
    val stock: StockType,
    val lotEntries: Map<String, Pair<Int?, String>>,
    val countGuid: String,
    val groupGuid: String,
    val transactionKey: String
)
