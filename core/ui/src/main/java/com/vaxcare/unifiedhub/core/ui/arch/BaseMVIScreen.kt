package com.vaxcare.unifiedhub.core.ui.arch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle

@Composable
inline fun <reified S : UiState, reified E : UiEvent, reified I : UiIntent> BaseMviScreen(
    viewModel: BaseViewModel<S, E, I>,
    noinline onEvent: (E) -> Unit,
    crossinline content: @Composable (S, (I) -> Unit) -> Unit
) {
    val currentHandler by rememberUpdatedState(onEvent)
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.uiEvent, lifecycleOwner) {
        viewModel.uiEvent
            .flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect(currentHandler)
    }

    content(state, viewModel::handleIntent)
}
