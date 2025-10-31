package com.vaxcare.unifiedhub.feature.pinin.ui

import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState

sealed interface PinInIntent : UiIntent {
    data object CloseScreen : PinInIntent

    data class DigitClicked(
        val digit: Char
    ) : PinInIntent

    data object DeleteDigit : PinInIntent

    data object ClearPin : PinInIntent

    data object AttemptPinIn : PinInIntent
}

sealed interface PinInEvent : UiEvent {
    data object NavigateBack : PinInEvent

    data object OnSuccess : PinInEvent
}

data class PinInState(
    val pinValue: String = "",
    val invalidPin: Boolean = false,
    val isKeypadEnabled: Boolean = true,
) : UiState
