package com.vaxcare.unifiedhub.feature.admin.ui.login

import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.datastore.datasource.DevicePreferenceDataSource
import com.vaxcare.unifiedhub.core.domain.ReportMetricEventUseCase
import com.vaxcare.unifiedhub.core.domain.ReportScreenEventsUseCase
import com.vaxcare.unifiedhub.core.domain.UpdateConnectivityStatusUseCase
import com.vaxcare.unifiedhub.core.model.ConnectivityStatus
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.feature.admin.metric.admin.AdminEvent
import com.vaxcare.unifiedhub.feature.admin.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminLoginViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val updateConnectivityStatus: UpdateConnectivityStatusUseCase,
    private val trackEvent: ReportMetricEventUseCase,
    private val trackScreen: ReportScreenEventsUseCase,
    private val devicePreferenceDataSource: DevicePreferenceDataSource
) : BaseViewModel<AdminLoginState, AdminLoginEvent, AdminLoginIntent>(
        AdminLoginState()
    ) {
    override fun start() {
        viewModelScope.launch(dispatcherProvider.io) {
            val serialNo = devicePreferenceDataSource.serialNumber.first()
            setState {
                copy(serialNo = serialNo)
            }

            reportScreen()
        }
    }

    override fun handleIntent(intent: AdminLoginIntent) {
        with(intent) {
            when (this) {
                is AdminLoginIntent.LogIn -> {
                    validatePassword(pw)
                }

                is AdminLoginIntent.Close -> {
                    sendEvent(AdminLoginEvent.NavigateBack)
                }

                is AdminLoginIntent.PasswordInput -> {
                    setState {
                        copy(pw = newValue)
                    }
                }
            }
        }
    }

    private fun reportScreen() {
        viewModelScope.launch(dispatcherProvider.io) {
            trackScreen("VaxCare Admin Login")
        }
    }

    fun validatePassword(pw: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            if (pw.isNotEmpty()) {
                setState {
                    copy(isLoading = true)
                }

                val isConnected = updateConnectivityStatus() == ConnectivityStatus.CONNECTED

                if (!isConnected) {
                    trackEvent(AdminEvent.AdminAuthenticationFailed)
                    setState {
                        copy(
                            isLoading = false,
                            currentError = AdminLoginError.DeviceOffline
                        )
                    }
                    return@launch
                }

                val isSuccessful = adminRepository.validatePassword(pw)

                if (isSuccessful) {
                    trackEvent(AdminEvent.AdminAuthenticated)
                    sendEvent(AdminLoginEvent.NavigateForward)
                } else {
                    trackEvent(AdminEvent.AdminAuthenticationFailed)
                    setState {
                        copy(
                            pw = "",
                            isLoading = false,
                            currentError = AdminLoginError.LoginFailure
                        )
                    }
                }
            }
        }
    }
}
