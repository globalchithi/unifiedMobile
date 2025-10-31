package com.vaxcare.unifiedhub.core.network.api

import com.vaxcare.unifiedhub.core.network.model.WrongProductNdcDto
import com.vaxcare.unifiedhub.core.network.util.IS_CALLED_BY_JOB
import retrofit2.http.GET
import retrofit2.http.Header

interface NdcApi {
    @GET("api/ndc/blacklisted")
    suspend fun getListOfWrongProductNdc(
        @Header(value = IS_CALLED_BY_JOB) isCalledByJob: Boolean = false
    ): List<WrongProductNdcDto>
}
