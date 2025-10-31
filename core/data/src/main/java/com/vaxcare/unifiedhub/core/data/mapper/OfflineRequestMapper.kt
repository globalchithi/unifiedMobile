package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.database.model.OfflineRequestEntity
import com.vaxcare.unifiedhub.core.network.model.OfflineRequestDTO
import javax.inject.Inject

class OfflineRequestMapper @Inject constructor() {
    fun networkToEntity(data: List<OfflineRequestDTO>) =
        data.map {
            OfflineRequestEntity(
                id = it.id,
                requestUri = it.requestUri,
                requestMethod = it.requestMethod,
                requestHeaders = it.requestHeaders,
                contentType = it.contentType,
                requestBody = it.requestBody
            )
        }

    fun entityToDomain(data: List<OfflineRequestEntity>) =
        data.map {
            OfflineRequestDTO(
                id = it.id,
                requestUri = it.requestUri,
                requestMethod = it.requestMethod,
                requestHeaders = it.requestHeaders,
                contentType = it.contentType,
                requestBody = it.requestBody,
                originalDateTime = it.originalDateTime
            )
        }
}
