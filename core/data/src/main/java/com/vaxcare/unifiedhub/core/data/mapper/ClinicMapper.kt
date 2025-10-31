package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.database.model.inventory.ClinicEntity
import com.vaxcare.unifiedhub.core.network.model.ClinicDTO
import javax.inject.Inject

class ClinicMapper @Inject constructor() {
    fun networkToEntity(data: ClinicDTO) =
        with(data) {
            ClinicEntity(
                id = id,
                name = name,
                clinicType = ClinicEntity.ClinicType.valueOf(type.name)
            )
        }
}
