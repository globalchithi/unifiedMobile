package com.vaxcare.unifiedhub.devtools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.datasource.DeviceNetworkProvider
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.data.repository.UserRepository
import com.vaxcare.unifiedhub.core.datastore.datasource.DevicePreferenceDataSource
import com.vaxcare.unifiedhub.core.domain.SetupVaxHub
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.analytics.events.CommonAnalyticsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevToolsViewModel @Inject constructor(
    deviceNetworkProvider: DeviceNetworkProvider,
    private val userRepository: UserRepository,
    devicePrefs: DevicePreferenceDataSource,
    private val locationRepository: LocationRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val setupVaxHub: SetupVaxHub,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {
    val networkData = deviceNetworkProvider.networkInfo
    val serialNumber = devicePrefs.serialNumber

    private val _syncState = MutableStateFlow(SyncState())
    val syncState = _syncState
        .onStart {
            viewModelScope.launch(dispatcherProvider.io) {
                locationRepository
                    .getLocation()
                    .combine(userRepository.getAllUsers()) { location, users ->
                        location?.clinicName to users
                    }.collectLatest { (clinicName, users) ->
                        _syncState.update {
                            it.copy(
                                loading = false,
                                clinicName = clinicName,
                                topPins = users.take(3).map { user -> user.pin },
                                totalUsers = users.size
                            )
                        }
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SyncState()
        )

    fun sendTestMetric() {
        viewModelScope.launch(dispatcherProvider.io) {
            analyticsRepository.track(CommonAnalyticsEvent.TestButtonPressed)
        }
    }

    fun triggerCrash(): Unit = throw RuntimeException("An exception was manually triggered.")

    fun triggerANR() {
        try {
            Thread.sleep(20000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun onSync(location: DevLocation) {
        viewModelScope.launch(dispatcherProvider.io) {
            with(location) {
                _syncState.update {
                    it.copy(loading = true)
                }

                val (_, tabletId) = locationRepository.getCheckData(pid, cid)
                setupVaxHub(
                    pid = pid,
                    cid = cid,
                    tabletId = tabletId,
                    scope = this@launch
                ).join()

                if (userRepository.needUsersSynced()) {
                    userRepository.forceSyncUsers()
                }
            }
        }
    }
}

data class SyncState(
    val loading: Boolean = false,
    val clinicName: String? = null,
    val topPins: List<String> = emptyList(),
    val totalUsers: Int = 0
)

enum class DevLocation(
    val pid: String,
    val cid: String,
) {
    CHURCH("100001", "10808"),
    CHRISTUS("178311", "89438"),
    LAKESIDE("176180", "83708"),
    GRINER("118452", "54549"),
    FIRST_PHYSICIANS("175272", "82001")
}
