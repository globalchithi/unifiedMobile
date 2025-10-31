package com.vaxcare.unifiedhub.feature.admin.ui.login

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.feature.admin.R

sealed interface AdminLoginIntent : UiIntent {
    data class LogIn(val pw: String) : AdminLoginIntent

    data object Close : AdminLoginIntent

    data class PasswordInput(val newValue: String) : AdminLoginIntent
}

sealed interface AdminLoginEvent : UiEvent {
    data object NavigateBack : AdminLoginEvent

    data object NavigateForward : AdminLoginEvent
}

data class AdminLoginState(
    val serialNo: String = "",
    val pw: String = "",
    val isLoading: Boolean = false,
    val currentError: AdminLoginError? = null
) : UiState

enum class AdminLoginError(
    @StringRes val stringRes: Int
) {
    LoginFailure(R.string.admin_login_error_password),
    DeviceOffline(R.string.admin_login_error_device_offline);

    @Composable
    fun getText(): String = stringResource(stringRes)
}
