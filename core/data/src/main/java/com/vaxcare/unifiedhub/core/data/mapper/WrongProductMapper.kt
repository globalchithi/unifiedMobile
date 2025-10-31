package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.database.model.inventory.WrongProductNdcEntity
import com.vaxcare.unifiedhub.core.model.product.WrongProductNdc
import com.vaxcare.unifiedhub.core.network.model.WrongProductNdcDto
import javax.inject.Inject

class WrongProductMapper @Inject constructor() {
    fun networkToEntity(data: List<WrongProductNdcDto>) =
        data.map {
            WrongProductNdcEntity(
                ndc = it.ndc,
                errorMessage = it.errorMessage
            )
        }

    fun entityToDomain(data: WrongProductNdcEntity?): WrongProductNdc? {
        if (data == null) return null
        return with(data) { WrongProductNdc(ndc, errorMessage) }
    }

    fun entityToDomain(data: List<WrongProductNdcEntity>) = data.mapNotNull { entityToDomain(it) }
}
