package com.vaxcare.unifiedhub.feature.pinin.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.ErrorMessage
import com.vaxcare.unifiedhub.core.ui.component.keypad.Keypad
import com.vaxcare.unifiedhub.core.ui.component.keypad.KeypadInputField
import com.vaxcare.unifiedhub.core.ui.component.keypad.playKeyPadSound
import com.vaxcare.unifiedhub.core.ui.compose.LocalAudioManager
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.feature.pinin.R
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun PinInScreen(
    navigateBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: PinInViewModel = hiltViewModel(),
) {
    BaseMviScreen<PinInState, PinInEvent, PinInIntent>(
        viewModel = viewModel,
        onEvent = {
            when (it) {
                PinInEvent.NavigateBack -> navigateBack()
                PinInEvent.OnSuccess -> {
                    onSuccess()
                }
            }
        }
    ) { state, handleIntent ->
        PinIn(
            state = state,
            handleIntent = handleIntent
        )
    }
}

@Composable
fun PinIn(
    state: PinInState,
    handleIntent: (PinInIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val audioManager = LocalAudioManager.current
    Scaffold(
        topBar = {
            BaseTitleBar(
                buttonIcon = DesignSystemR.drawable.ic_close,
                onButtonClick = { handleIntent(PinInIntent.CloseScreen) }
            )
        },
        containerColor = color.container.primaryContainer,
        modifier = modifier
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(Modifier.height(50.dp)) {
                ErrorMessage(
                    text = R.string.invalid_pin,
                    isError = state.invalidPin,
                    modifier = Modifier
                        .height(measurement.size.input)
                        .padding(top = measurement.spacing.large)
                )
            }
            KeypadInputField(
                visualTransformation = PasswordVisualTransformation(mask = Char(42)),
                textStyle = type.bodyTypeStyle.body1Bold.copy(
                    textAlign = TextAlign.Center,
                    letterSpacing = 20.sp,
                    lineHeight = 1.em,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.LastLineBottom
                    )
                ),
                value = state.pinValue,
                onClearClick = { handleIntent(PinInIntent.ClearPin) }
            )
            Keypad(
                isDeleteEnabled = state.pinValue.isNotEmpty(),
                isConfirmEnabled = state.pinValue.isNotEmpty(),
                isKeypadEnabled = state.isKeypadEnabled,
                onPlaySound = { audioManager?.playKeyPadSound() },
                onDigitClick = { handleIntent(PinInIntent.DigitClicked(it)) },
                onDeleteClick = { handleIntent(PinInIntent.DeleteDigit) },
                onConfirmClick = { handleIntent(PinInIntent.AttemptPinIn) }
            )
        }
    }
}

@FullDevicePreview
@Composable
private fun Default() {
    PinIn(
        PinInState(
            invalidPin = true,
            pinValue = ""
        ),
        {}
    )
}
