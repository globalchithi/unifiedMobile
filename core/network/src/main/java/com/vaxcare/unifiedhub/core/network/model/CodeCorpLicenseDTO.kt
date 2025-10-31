package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class CodeCorpLicenseDTO(
    val type: String,
    val customerId: String,
    val key: String,
    val expiration: LocalDateTime
)
