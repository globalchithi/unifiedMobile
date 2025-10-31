package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.data.mapper.OfflineRequestInfoMapper
import com.vaxcare.unifiedhub.core.data.mapper.OfflineRequestMapper
import com.vaxcare.unifiedhub.core.database.dao.OfflineRequestDao
import com.vaxcare.unifiedhub.core.network.model.OfflineRequestDTO
import com.vaxcare.unifiedhub.core.network.model.OfflineRequestInfoDTO
import timber.log.Timber
import javax.inject.Inject

interface OfflineRequestRepository {
    suspend fun getAllAsync(): List<OfflineRequestDTO>

    suspend fun getOfflineRequestInfo(): List<OfflineRequestInfoDTO>

    suspend fun deleteByIds(ids: List<Int>)

    fun insertOfflineRequest(request: OfflineRequestDTO)
}

class OfflineRequestRepositoryImpl @Inject constructor(
    private val offlineRequestDao: OfflineRequestDao,
    private val offlineRequestMapper: OfflineRequestMapper,
    private val offlineRequestInfoMapper: OfflineRequestInfoMapper
) : OfflineRequestRepository {
    override suspend fun getAllAsync(): List<OfflineRequestDTO> {
        val entities = offlineRequestDao.getAllAsync()
        return offlineRequestMapper.entityToDomain(entities)
    }

    override suspend fun getOfflineRequestInfo(): List<OfflineRequestInfoDTO> {
        val entities = offlineRequestDao.getOfflineRequestList()
        return offlineRequestInfoMapper.entityToDomain(entities)
    }

    override suspend fun deleteByIds(ids: List<Int>) {
        offlineRequestDao.deleteOfflineRequestsByIds(ids)
    }

    override fun insertOfflineRequest(request: OfflineRequestDTO) {
        Timber.d("Saving offline request: ${request.requestUri}")
        offlineRequestDao.insert(offlineRequestMapper.networkToEntity(listOf(request)))
    }
}
