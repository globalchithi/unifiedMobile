package com.vaxcare.unifiedhub.core.model.product

data class WrongProductNdc(
    val ndc: String,
    val errorMessage: String,
)
