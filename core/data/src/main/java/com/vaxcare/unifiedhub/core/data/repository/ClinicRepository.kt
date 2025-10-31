package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.data.mapper.ClinicMapper
import com.vaxcare.unifiedhub.core.database.dao.ClinicDao
import com.vaxcare.unifiedhub.core.network.api.PatientsApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ClinicRepository {
    suspend fun syncClinics(isCalledByJob: Boolean = false)

    fun getNoOfPermanentClinics(): Flow<Int>
}

class ClinicRepositoryImpl @Inject constructor(
    private val patientsApi: PatientsApi,
    private val clinicDao: ClinicDao,
    private val clinicMapper: ClinicMapper,
) : ClinicRepository {
    override suspend fun syncClinics(isCalledByJob: Boolean) {
        val responseBody = patientsApi.getClinics(isCalledByJob).body() ?: return
        clinicDao.replaceAll(responseBody.map(clinicMapper::networkToEntity))
    }

    override fun getNoOfPermanentClinics() = clinicDao.getNoOfPermanentClinics()
}
