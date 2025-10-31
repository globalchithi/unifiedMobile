package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeatureFlagDTO(
    val featureFlagId: Int,
    val clinicId: Int = 0,
    val featureFlagName: String
)
