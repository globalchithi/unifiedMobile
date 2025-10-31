package com.vaxcare.unifiedhub.core.database.converter

import androidx.room.TypeConverter
import com.vaxcare.unifiedhub.core.database.model.enums.Gender
import com.vaxcare.unifiedhub.core.database.model.enums.PresentationDTO
import com.vaxcare.unifiedhub.core.database.model.enums.ProductCategory
import com.vaxcare.unifiedhub.core.database.model.enums.ProductStatus
import com.vaxcare.unifiedhub.core.database.model.enums.RouteCode

class ProductEnumTypeConverters {
    @TypeConverter
    fun fromProductCategory(cat: ProductCategory): Int = cat.id

    @TypeConverter
    fun toProductCategory(int: Int) = ProductCategory.fromInt(int)

    @TypeConverter
    fun fromProductStatus(status: ProductStatus) = status.id

    @TypeConverter
    fun toProductStatus(int: Int) = ProductStatus.fromInt(int)

    @TypeConverter
    fun fromRouteCode(route: RouteCode) = route.ordinal

    @TypeConverter
    fun toRouteCode(int: Int) = RouteCode.fromInt(int)

    @TypeConverter
    fun fromProductPresentation(pres: PresentationDTO) = pres.ordinal

    @TypeConverter
    fun toProductPresentation(int: Int) = PresentationDTO.fromInt(int)

    @TypeConverter
    fun fromGender(gender: Gender) = gender.ordinal

    @TypeConverter
    fun toGender(int: Int) = Gender.fromInt(int)
}
