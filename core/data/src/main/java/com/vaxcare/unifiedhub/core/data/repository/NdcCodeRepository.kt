package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.database.dao.NdcCodeDao
import javax.inject.Inject

class NdcCodeRepository @Inject constructor(
    private val dao: NdcCodeDao
) {
    suspend fun getProductIdByNdcCode(ndcCode: String): Int? = dao.getProductIdByNdcCode(ndcCode)
}
