package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.database.model.UserEntity
import com.vaxcare.unifiedhub.core.model.User
import com.vaxcare.unifiedhub.core.network.model.UserNetworkDTO
import javax.inject.Inject

class UserMapper @Inject constructor() {
    fun networkToEntity(data: UserNetworkDTO) =
        with(data) {
            UserEntity(
                firstName = firstName ?: "",
                lastName = lastName ?: "",
                pin = pin,
                userId = userId,
                userName = userName ?: ""
            )
        }

    fun entityToDomain(data: UserEntity) =
        with(data) {
            User(
                firstName = firstName,
                lastName = lastName,
                pin = pin,
                userId = userId,
                userName = userName
            )
        }
}
