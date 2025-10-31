package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WrongProductNdcDto(
    val ndc: String,
    val errorMessage: String
)
