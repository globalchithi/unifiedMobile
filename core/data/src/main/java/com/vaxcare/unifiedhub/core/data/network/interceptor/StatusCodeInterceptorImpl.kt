package com.vaxcare.unifiedhub.core.data.network.interceptor

import com.vaxcare.unifiedhub.core.data.device.NetworkMonitor
import com.vaxcare.unifiedhub.core.data.repository.OfflineRequestRepository
import com.vaxcare.unifiedhub.core.network.interceptor.StatusCodeInterceptor
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatusCodeInterceptorImpl @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val offlineRequestValidator: OfflineRequestValidator,
    private val offlineRequestRepository: OfflineRequestRepository
) : StatusCodeInterceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            if (e.message != "Canceled") {
                Timber.d("An IOException occurred while attempting to make a request, ${e.printStackTrace()}")
                val url = request.url.toUri().toASCIIString()
                Timber.d("URL: $url")
                storeOfflineRequest(request)
                throw e
            } else {
                throw e
            }
        }

        if (request.url
                .toUri()
                .toASCIIString()
                .contains("vaxcare.com")
        ) {
            networkMonitor.handleVaxcareResponseCode(response.code)

            if (!response.isSuccessful) {
                storeOfflineRequest(request)
            }
        }

        return response
    }

    private fun storeOfflineRequest(request: Request) {
        offlineRequestValidator.validateRequest(request = request)?.let { offlineRequest ->
            offlineRequestRepository.insertOfflineRequest(offlineRequest)
        }
    }
}
