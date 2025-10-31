package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class UpdateSeverityDTO {
    NoAction,
    Warning,
    Blocker
}
