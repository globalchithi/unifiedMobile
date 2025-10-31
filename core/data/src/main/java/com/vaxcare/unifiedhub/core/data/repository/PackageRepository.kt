package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.data.mapper.PackageMapper
import com.vaxcare.unifiedhub.core.database.dao.PackageDao
import javax.inject.Inject

class PackageRepository @Inject constructor(
    private val dao: PackageDao,
    private val mapper: PackageMapper
) {
    suspend fun getOneByProductId(productId: Int) =
        dao
            .getOneByProductId(productId)
            .let(mapper::entityToDomain)
}
