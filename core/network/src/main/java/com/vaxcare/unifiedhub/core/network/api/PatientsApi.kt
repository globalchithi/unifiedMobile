package com.vaxcare.unifiedhub.core.network.api

import com.vaxcare.unifiedhub.core.network.model.ClinicDTO
import com.vaxcare.unifiedhub.core.network.util.IS_CALLED_BY_JOB
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface PatientsApi {
    @GET("api/patients/clinic")
    suspend fun getClinics(
        @Header(IS_CALLED_BY_JOB) isCalledByJob: Boolean = false
    ): Response<List<ClinicDTO>>
}
