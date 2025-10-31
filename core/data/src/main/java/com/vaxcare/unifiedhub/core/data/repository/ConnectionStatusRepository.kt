package com.vaxcare.unifiedhub.core.data.repository

interface ConnectionStatusRepository {
    suspend fun isDeviceOnline(): Boolean

    suspend fun isVaxcareReachable(): Boolean
}
