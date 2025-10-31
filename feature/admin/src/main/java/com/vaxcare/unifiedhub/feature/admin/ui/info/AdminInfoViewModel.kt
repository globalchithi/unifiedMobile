package com.vaxcare.unifiedhub.feature.admin.ui.info

import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.BuildConfig
import com.vaxcare.unifiedhub.core.datastore.datasource.DevicePreferenceDataSource
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AdminInfoViewModel @Inject constructor(
    val dispatcherProvider: DispatcherProvider,
    val devicePreferenceRepository: DevicePreferenceDataSource
) : BaseViewModel<AdminInfoState, AdminInfoEvent, AdminInfoIntent>(
        initialState = AdminInfoState()
    ) {
    override fun start() {
        loadScreenInformation()
    }

    override fun handleIntent(intent: AdminInfoIntent) {
        Timber.d("Handling intent: $intent")
        when (intent) {
            AdminInfoIntent.CloseScreen -> sendEvent(AdminInfoEvent.NavigateBack)
            AdminInfoIntent.OpenSourceLibrary -> sendEvent(AdminInfoEvent.NavigateToOpenSourceLibrary)
            AdminInfoIntent.OpenSystemConnectivity -> sendEvent(AdminInfoEvent.NavigateToSystemConnectivity)
            AdminInfoIntent.ValidateScannerLicenseClicked -> setState {
                copy(activeDialog = AdminInfoDialog.ValidateScannerLicense)
            }

            AdminInfoIntent.CloseValidateScanner -> setState { copy(activeDialog = null) }
        }
    }

    private fun loadScreenInformation() {
        viewModelScope.launch(dispatcherProvider.io) {
            setState { copy(isLoading = true) }
            devicePreferenceRepository.serialNumber.collect {
                setState {
                    copy(
                        apkVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                        serialNumber = it,
                        lastSyncedDatabaseRecords = LocalDateTime.of(1900, 1, 1, 0, 0),
                        activeDialog = activeDialog,
                        isLoading = false
                    )
                }
            }
        }
    }
}
