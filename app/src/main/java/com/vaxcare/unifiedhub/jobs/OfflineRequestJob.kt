package com.vaxcare.unifiedhub.jobs

import com.squareup.moshi.Moshi
import com.vaxcare.unifiedhub.core.data.network.interceptor.OfflineRequestHandler
import com.vaxcare.unifiedhub.core.data.repository.OfflineRequestRepository
import com.vaxcare.unifiedhub.core.network.model.OfflineRequestDTO
import com.vaxcare.unifiedhub.core.network.util.IS_CALLED_BY_JOB
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.vaxjob.model.BaseVaxJob
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineRequestJob @Inject constructor(
    private val repository: OfflineRequestRepository,
    private val moshi: Moshi,
    private val httpClient: OkHttpClient,
    private val responseHandler: OfflineRequestHandler,
    analyticsRepository: AnalyticsRepository
) : BaseVaxJob(analyticsRepository) {
    private val maxCursorSizeKb = 200000
    private var running = false

    override suspend fun doWork(parameter: Any?) {
        if (!running) {
            running = true
            val offlineRequestList = repository.getOfflineRequestInfo()
            if (offlineRequestList.any { it.bodySize > maxCursorSizeKb }) {
                Timber.e("Deleting Offline Requests where body sizes are > 2MB")
                val tooLargeRequestIds = offlineRequestList
                    .filter { it.bodySize > maxCursorSizeKb }
                    .map { it.id }
                repository.deleteByIds(tooLargeRequestIds)
            }

            // todo save metric?

            // get OfflineRequests
            Timber.d("Getting OfflineRequests...")
            val offlineRequests = repository.getAllAsync()

            // fire OfflineRequests
            var successfulRequestCount = 0

            Timber.d("Firing OfflineRequests...")
            offlineRequests.forEachIndexed { index, offlineRequest ->
                val requestNum = index + 1

                Timber.d("Firing OfflineRequest ${offlineRequest.requestUri} $requestNum...")
                try {
                    val requestBody = offlineRequest.requestBody

                    requestBody.let { body ->
                        Timber.d("OfflineRequest $requestNum processing response...")
                        val response = fireRequestAndGetResponse(offlineRequest, body)

                        responseHandler.handleResponse(response)
                        if (response.isSuccessful) {
                            Timber.d("OfflineRequest $requestNum succeeded")
                            Timber.d("Removing OfflineRequest $requestNum from queue...")

                            repository.deleteByIds(listOf(offlineRequest.id))

                            successfulRequestCount += 1
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(
                        e,
                        "An exception occurred while replaying OfflineRequest $requestNum"
                    )
                }
            }

            running = false
            Timber.d("OfflineRequest work complete")
        }
    }

    private fun fireRequestAndGetResponse(offlineRequest: OfflineRequestDTO, body: String): Response {
        val requestBuilder = Request
            .Builder()
            .url(offlineRequest.requestUri)
            .headers(
                moshi
                    .adapter(Headers::class.java)
                    .fromJson(offlineRequest.requestHeaders)!!
            ).method(
                offlineRequest.requestMethod,
                body.toRequestBody(
                    moshi
                        .adapter(MediaType::class.java)
                        .fromJson(offlineRequest.contentType)
                )
            )

        if (!offlineRequest.requestHeaders.contains(IS_CALLED_BY_JOB)) {
            requestBuilder.headers(Headers.Builder().add(IS_CALLED_BY_JOB, "true").build())
        }

        return httpClient.newCall(requestBuilder.build()).execute()
    }
}
