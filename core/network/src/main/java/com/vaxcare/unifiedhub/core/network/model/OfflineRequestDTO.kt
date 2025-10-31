package com.vaxcare.unifiedhub.core.network.model

import java.time.LocalDateTime

data class OfflineRequestDTO(
    val id: Int,
    val requestUri: String,
    val requestMethod: String,
    val requestHeaders: String,
    val contentType: String,
    val requestBody: String,
    val originalDateTime: LocalDateTime?
)

data class OfflineRequestInfoDTO(
    val id: Int,
    val requestUri: String,
    val bodySize: Int,
    val originalDateTime: LocalDateTime?
)
