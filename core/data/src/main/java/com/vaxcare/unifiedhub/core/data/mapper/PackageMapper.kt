package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.database.model.inventory.product.PackageEntity
import javax.inject.Inject
import com.vaxcare.unifiedhub.core.model.product.Package as PackageVC

class PackageMapper @Inject constructor() {
    fun entityToDomain(data: PackageEntity) =
        with(data) {
            PackageVC(
                id = id,
                productId = productId,
                itemCount = itemCount
            )
        }
}
