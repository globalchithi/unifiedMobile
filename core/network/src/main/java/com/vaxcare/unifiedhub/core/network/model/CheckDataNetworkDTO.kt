package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckDataNetworkDTO(
    val result: Boolean,
    val tabletId: String
)
