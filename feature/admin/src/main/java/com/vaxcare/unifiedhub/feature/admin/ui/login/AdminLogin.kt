package com.vaxcare.unifiedhub.feature.admin.ui.login

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.PasswordInputField
import com.vaxcare.unifiedhub.core.ui.component.button.PrimaryButton
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.feature.admin.R
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemResource

@Composable
fun AdminLoginScreen(
    viewmodel: AdminLoginViewModel = hiltViewModel<AdminLoginViewModel>(),
    onBackClick: () -> Unit,
    onLoginSuccess: () -> Unit,
) {
    BaseMviScreen(
        viewModel = viewmodel,
        onEvent = { event ->
            when (event) {
                is AdminLoginEvent.NavigateBack -> {
                    onBackClick()
                }

                is AdminLoginEvent.NavigateForward -> {
                    onLoginSuccess()
                }
            }
        }
    ) { state, handleIntent ->
        AdminLoginScreen(state, handleIntent)
    }
}

@Composable
fun AdminLoginScreen(
    state: AdminLoginState,
    handleIntent: (AdminLoginIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            BaseTitleBar(
                title = R.string.admin_login_title,
                buttonIcon = DesignSystemResource.drawable.ic_close,
                onButtonClick = { handleIntent(AdminLoginIntent.Close) }
            )
        },
    ) {
        AdminLogin(
            serialNo = state.serialNo,
            isLoading = state.isLoading,
            errorMessage = state.currentError?.getText(),
            password = state.pw,
            onLoginClick = { handleIntent(AdminLoginIntent.LogIn(state.pw)) },
            onPasswordChange = { pw -> handleIntent(AdminLoginIntent.PasswordInput(pw)) },
            modifier = modifier.padding(it),
        )
    }
}

@Composable
private fun AdminLogin(
    errorMessage: String?,
    isLoading: Boolean,
    serialNo: String,
    password: String,
    modifier: Modifier = Modifier,
    onPasswordChange: (String) -> Unit,
    onLoginClick: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val localConfig = LocalConfiguration.current

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(color.surface.surface)
            .fillMaxSize()
    ) {
        Spacer(Modifier.height(measurement.spacing.xSmall))
        Text(
            text = errorMessage ?: "",
            style = type.bodyTypeStyle.body4,
            color = color.onContainer.error
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                if (localConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    25.dp
                } else {
                    144.dp
                }
            )
        ) {
            PasswordInputField(
                initialLabel = stringResource(
                    R.string.admin_login_enter_password_fmt,
                    serialNo
                ),
                value = password,
                isEnabled = !isLoading,
                onClearClick = { onPasswordChange("") },
                onPasswordChange = {
                    if (it.length < 16) {
                        onPasswordChange(it)
                    }
                },
                onKeyboardDone = {
                    onLoginClick(password)
                },
                modifier = Modifier
                    .testTag(TestTags.AdminLogin.PASSWORD_FIELD)
                    .focusRequester(focusRequester)
            )

            PrimaryButton(
                onClick = { onLoginClick(password) },
                enabled = password.isNotEmpty() && !isLoading,
                modifier = Modifier
                    .requiredWidth(272.dp)
                    .requiredHeight(56.dp)
                    .testTag(TestTags.AdminLogin.LOGIN_BUTTON),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    Text(
                        text = stringResource(R.string.admin_login_button),
                        style = type.bodyTypeStyle.body3Bold,
                        color = color.onContainer.primaryInverse
                    )
                }
            }
        }
    }

    LaunchedEffect(localConfig.orientation) { focusRequester.requestFocus() }
}

@FullDevicePreview
@Composable
private fun AdminLoginPreview() {
    VaxCareTheme {
        AdminLoginScreen(
            state = AdminLoginState(
                serialNo = "VAX123",
                pw = "333"
            ),
            handleIntent = {}
        )
    }
}
