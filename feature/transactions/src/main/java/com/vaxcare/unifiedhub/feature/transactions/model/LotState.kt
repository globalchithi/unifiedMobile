package com.vaxcare.unifiedhub.feature.transactions.model

import com.vaxcare.unifiedhub.feature.transactions.domain.model.TransactionSession
import java.util.UUID

/**
 * State holder for lot-level changes made during a [TransactionSession]
 */
data class LotState(
    val delta: Int? = 0,
    val isDeleted: Boolean = false,
    val receiptKey: String = UUID.randomUUID().toString()
)
