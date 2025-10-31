package com.vaxcare.unifiedhub.core.ui.arch

import androidx.annotation.OpenForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel<S : UiState, E : UiEvent, I : UiIntent>(
    initialState: S
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState
        .onStart { start() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = initialState
        )

    private val _uiEvent = MutableSharedFlow<E>()
    val uiEvent: SharedFlow<E> = _uiEvent.asSharedFlow()

    @OpenForTesting
    fun currentState(): S = _uiState.value

    @OpenForTesting
    fun setState(reducer: S.() -> S) {
        val previousState = _uiState.value
        val newState = previousState.reducer()
        Timber.tag(this.javaClass.simpleName).d("State changed: \nPrevious: $previousState\nNew: $newState")
        _uiState.value = newState
    }

    protected fun sendEvent(event: E) {
        Timber.tag(this.javaClass.simpleName).d("Event sent: $event")
        viewModelScope.launch { _uiEvent.emit(event) }
    }

    abstract fun handleIntent(intent: I)

    /**
     * IMPORTANT: Use this instead of ViewModel's init {} block.
     * The use of init {} breaks testability since it happens before runTest {};
     * which is used to take control over the Kotlin coroutines for testing purposes.
     */
    open fun start() {
        // Nothing by default.
    }
}
