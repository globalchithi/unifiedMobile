package com.vaxcare.unifiedhub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.datasource.DeviceNetworkProvider
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import com.vaxcare.unifiedhub.core.model.ConnectivityStatus
import com.vaxcare.unifiedhub.jobs.OfflineRequestJob
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.analytics.events.CommonAnalyticsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
    private val offlineRequestJob: OfflineRequestJob,
    deviceNetworkProvider: DeviceNetworkProvider,
    private val userSessionPrefs: UserSessionPreferenceDataSource,
    dispatcherProvider: DispatcherProvider
) : ViewModel() {
    init {
        viewModelScope.launch(dispatcherProvider.io) {
            analyticsRepository.track(CommonAnalyticsEvent.ScreenView(screenName = "Hello World"))
            deviceNetworkProvider.networkInfo.collectLatest {
                when (it.connectivityStatus) {
                    ConnectivityStatus.CONNECTED -> {
                        Timber.d("Network Status is connected, running offlineRequestJob...")
                        offlineRequestJob.doWork()
                    }

                    ConnectivityStatus.CONNECTED_VAXCARE_UNREACHABLE,
                    ConnectivityStatus.CONNECTED_NO_INTERNET,
                    ConnectivityStatus.DISCONNECTED -> Timber.d("Network Status changed: ${it.connectivityStatus}")
                }
            }
        }
    }

    suspend fun clearUserSession() {
        userSessionPrefs.clearUserSession()
    }
}
