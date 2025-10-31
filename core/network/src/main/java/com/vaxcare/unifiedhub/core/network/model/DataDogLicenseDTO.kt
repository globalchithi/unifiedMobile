package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataDogLicenseDTO(
    val clientToken: String,
    val applicationId: String,
    @Json(name = "sampleRate")
    val rumSampleRate: Float,
    val sessionReplaySampleRate: Float,
    val site: String,
    val enabled: Boolean
)
