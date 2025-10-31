package com.vaxcare.unifiedhub.core.network.api

import com.vaxcare.unifiedhub.core.network.model.CheckDataNetworkDTO
import com.vaxcare.unifiedhub.core.network.model.LocationNetworkDTO
import com.vaxcare.unifiedhub.core.network.model.SetupConfigDTO
import com.vaxcare.unifiedhub.core.network.model.UserNetworkDTO
import com.vaxcare.unifiedhub.core.network.util.IS_CALLED_BY_JOB
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SetupApi {
    @GET("api/setup/ValidatePassword")
    suspend fun validatePassword(
        @Query("password") password: String
    ): Response<Boolean>

    @GET("api/setup/usersPartnerLevel")
    suspend fun getUsersForPartner(
        @Query("partnerId") partnerId: String,
        @Header(value = IS_CALLED_BY_JOB) isCalledByJob: Boolean = false
    ): Response<List<UserNetworkDTO>>

    @GET("api/setup/checkData")
    suspend fun getPidCidCheck(
        @Query("partnerId") partnerId: String,
        @Query("clinicId") clinicId: String
    ): Response<CheckDataNetworkDTO>

    @GET("api/setup/LocationData")
    suspend fun getLocationData(
        @Query("clinicId") clinicId: String,
        @Header(value = IS_CALLED_BY_JOB) isCalledByJob: Boolean = false
    ): Response<LocationNetworkDTO>

    @GET("api/ping")
    suspend fun pingVaxCareServer(): Response<Unit>

    @GET("api/setup/config")
    suspend fun getSetupConfig(
        @Query("isOffline") isOffline: String,
        @Header(value = IS_CALLED_BY_JOB) isCalledByJob: Boolean = false
    ): SetupConfigDTO
}
