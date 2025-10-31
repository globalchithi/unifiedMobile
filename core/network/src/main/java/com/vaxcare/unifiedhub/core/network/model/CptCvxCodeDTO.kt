package com.vaxcare.unifiedhub.core.network.model

data class CptCvxCodeDTO(
    val cptCode: String,
    val cvxCode: String,
    val isMedicare: Int,
    val productId: Int
)
