package com.vaxcare.unifiedhub.core.database.model.inventory

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "Clinics")
data class ClinicEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val clinicType: ClinicType
) {
    @JsonClass(generateAdapter = false)
    enum class ClinicType {
        Permanent,
        Temporary
    }
}
