package com.vaxcare.unifiedhub.core.network.api

import com.vaxcare.unifiedhub.core.network.model.AdjustmentEntryRequestDTO
import com.vaxcare.unifiedhub.core.network.model.CountRequestDTO
import com.vaxcare.unifiedhub.core.network.model.CountResponseDTO
import com.vaxcare.unifiedhub.core.network.model.LegacyProductMappingDTO
import com.vaxcare.unifiedhub.core.network.model.LotInventoryResponseDTO
import com.vaxcare.unifiedhub.core.network.model.LotNumberDTO
import com.vaxcare.unifiedhub.core.network.model.PostDTO
import com.vaxcare.unifiedhub.core.network.model.ProductDTO
import com.vaxcare.unifiedhub.core.network.model.returns.ReturnDTO
import com.vaxcare.unifiedhub.core.network.util.IS_CALLED_BY_JOB
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface InventoryApi {
    @GET("api/inventory/lotnumbers")
    suspend fun getLotNumbers(
        @Header(value = IS_CALLED_BY_JOB) isCalledByJob: Boolean = false,
        @Query("maximumExpirationAgeInDays") expiredCutoffDays: Int? = null
    ): List<LotNumberDTO>

    @GET("api/inventory/product/v2")
    suspend fun getProducts(
        @Header(value = IS_CALLED_BY_JOB) isCalledByJob: Boolean = false
    ): List<ProductDTO>

    @POST("api/inventory/lotnumbers")
    suspend fun postLotNumber(
        @Body lotNumber: LotNumberDTO,
        @Header(value = IS_CALLED_BY_JOB) isCalledByJob: Boolean = false
    ): List<LotNumberDTO>

    @GET("api/inventory/lotInventory")
    suspend fun getLotInventory(
        @Header(value = IS_CALLED_BY_JOB) isCalledByJob: Boolean = false
    ): LotInventoryResponseDTO

    @GET("api/inventory/product/mappings")
    suspend fun getProductMappings(
        @Header(value = IS_CALLED_BY_JOB) isCalledByJob: Boolean = false
    ): List<LegacyProductMappingDTO>

    @POST("api/inventory/vaccinecountconfirmation/get")
    suspend fun getVaccineCount(
        @Body guids: PostDTO<List<String>>,
        @Header(value = IS_CALLED_BY_JOB) isCalledByJob: Boolean = false
    ): List<CountResponseDTO>?

    @POST("api/inventory/vaccinecountconfirmation")
    suspend fun postCount(
        @Body body: PostDTO<CountRequestDTO>
    )

    @POST("api/adjustments")
    suspend fun postAdjustments(
        @Body body: PostDTO<List<AdjustmentEntryRequestDTO>>
    )

    @POST("api/return")
    suspend fun postReturn(
        @Body payload: ReturnDTO
    )
}
