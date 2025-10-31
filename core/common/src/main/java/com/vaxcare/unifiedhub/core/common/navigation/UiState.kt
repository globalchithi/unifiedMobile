package com.vaxcare.unifiedhub.core.common.navigation

interface UiState

sealed class UiStates : UiState {
    data object Init : UiStates()

    data object Loading : UiStates()
}
