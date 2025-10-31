package com.vaxcare.unifiedhub.core.network.api

import com.vaxcare.unifiedhub.core.network.model.PickupAvailabilityResponseDto
import retrofit2.http.GET

interface ReturnApi {
    @GET("api/return/pickupAvailability")
    suspend fun getPickUpAvailability(): List<PickupAvailabilityResponseDto>
}
