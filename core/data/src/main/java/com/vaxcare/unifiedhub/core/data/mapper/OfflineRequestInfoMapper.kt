package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.database.model.OfflineRequestInfo
import com.vaxcare.unifiedhub.core.network.model.OfflineRequestInfoDTO
import javax.inject.Inject

class OfflineRequestInfoMapper @Inject constructor() {
    fun networkToEntity(data: List<OfflineRequestInfoDTO>) =
        data.map {
            OfflineRequestInfo(
                id = it.id,
                requestUri = it.requestUri,
                bodySize = it.bodySize,
                originalDateTime = it.originalDateTime
            )
        }

    fun entityToDomain(data: List<OfflineRequestInfo>) =
        data.map {
            OfflineRequestInfoDTO(
                id = it.id,
                requestUri = it.requestUri,
                bodySize = it.bodySize,
                originalDateTime = it.originalDateTime
            )
        }
}
