package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.data.mapper.WrongProductMapper
import com.vaxcare.unifiedhub.core.database.dao.WrongProductNdcDao
import com.vaxcare.unifiedhub.core.model.product.WrongProductNdc
import com.vaxcare.unifiedhub.core.network.api.NdcApi
import javax.inject.Inject

interface WrongProductNdcRepository {
    suspend fun getAndUpsertWrongProductNdcs(isCalledByJob: Boolean): List<WrongProductNdc>

    suspend fun getWrongProductByNdc(ndc: String): WrongProductNdc?
}

class WrongProductNdcRepositoryImpl @Inject constructor(
    private val ndcApi: NdcApi,
    private val wrongProductDao: WrongProductNdcDao,
    private val mapper: WrongProductMapper
) : WrongProductNdcRepository {
    override suspend fun getAndUpsertWrongProductNdcs(isCalledByJob: Boolean): List<WrongProductNdc> {
        val blacklistedProducts = ndcApi.getListOfWrongProductNdc(isCalledByJob)
        val entities = mapper.networkToEntity(blacklistedProducts)
        with(wrongProductDao) {
            deleteAll()
            insert(entities)
        }
        return mapper.entityToDomain(entities)
    }

    override suspend fun getWrongProductByNdc(ndc: String): WrongProductNdc? =
        mapper.entityToDomain(wrongProductDao.getByNdc(ndc))
}
