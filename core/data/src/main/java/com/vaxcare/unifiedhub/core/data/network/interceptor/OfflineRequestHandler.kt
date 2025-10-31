package com.vaxcare.unifiedhub.core.data.network.interceptor

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.database.model.OfflineRequestEntity
import com.vaxcare.unifiedhub.core.network.model.LotNumberDTO
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineRequestHandler @Inject constructor(
    private val moshi: Moshi,
    private val lotRepository: LotRepository
) {
    suspend fun handleResponse(response: Response) {
        if (response.isSuccessful) {
            val requestUri = response.request.url
                .toUri()
                .toASCIIString()
            if (requestUri.contains(Regex(OfflineRequestEntity.LOT_CREATION))) {
                val type = Types.newParameterizedType(List::class.java, LotNumberDTO::class.java)
                val jsonAdapter: JsonAdapter<List<LotNumberDTO>> = moshi.adapter(type)
                val lotNumberList = response.body?.source()?.let { jsonAdapter.fromJson(it) }
                lotNumberList?.let { lotRepository.insertLots(it) }
            }
        }
    }
}
