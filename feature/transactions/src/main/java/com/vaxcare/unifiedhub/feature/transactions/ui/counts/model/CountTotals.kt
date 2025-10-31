package com.vaxcare.unifiedhub.feature.transactions.ui.counts.model

import kotlinx.serialization.Serializable

@Serializable
data class CountTotals(
    val products: Int? = null,
    val units: Int? = null,
    val addedUnits: Int? = null,
    val addedImpact: Float? = null,
    val missingUnits: Int? = null,
    val missingImpact: Float? = null,
)
