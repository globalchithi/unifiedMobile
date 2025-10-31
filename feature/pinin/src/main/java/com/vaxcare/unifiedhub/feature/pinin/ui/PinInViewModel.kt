package com.vaxcare.unifiedhub.feature.pinin.ui

import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.repository.UserRepository
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.feature.pinin.model.PinInStatus
import com.vaxcare.unifiedhub.feature.pinin.model.PinningStatus
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PinInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val userSessionPreferenceDataSource: UserSessionPreferenceDataSource,
    private val analyticsRepository: AnalyticsRepository
) : BaseViewModel<PinInState, PinInEvent, PinInIntent>(
        initialState = PinInState()
    ) {
    override fun handleIntent(intent: PinInIntent) {
        when (intent) {
            PinInIntent.CloseScreen -> sendEvent(PinInEvent.NavigateBack)
            PinInIntent.AttemptPinIn -> doPinIn()
            PinInIntent.ClearPin -> setPinValue("")
            PinInIntent.DeleteDigit -> setPinValue(currentState().pinValue.dropLast(1))
            is PinInIntent.DigitClicked -> digitClicked(intent.digit)
        }
    }

    private fun setPinValue(newValue: String) {
        if (newValue.length <= 4) {
            val isKeypadEnabled = if (newValue.length == 4) false else true
            setState {
                copy(
                    pinValue = newValue,
                    invalidPin = false,
                    isKeypadEnabled = isKeypadEnabled
                )
            }
        }
    }

    private fun digitClicked(digit: Char) {
        setPinValue(currentState().pinValue + digit)
    }

    private fun doPinIn() {
        viewModelScope.launch(dispatcherProvider.io) {
            runCatching { userRepository.getUser(currentState().pinValue) }
                .onSuccess {
                    it?.let {
                        userSessionPreferenceDataSource.setUserSession(
                            it.userId.toLong(),
                            it.userName
                        )
                        analyticsRepository.track(
                            PinInStatus(
                                pinningStatus = PinningStatus.SUCCESS,
                                pinUsed = currentState().pinValue,
                                username = it.userName
                            )
                        )
                        sendEvent(PinInEvent.OnSuccess)
                    } ?: handleInvalidPin()
                }.onFailure {
                    analyticsRepository.track(
                        PinInStatus(
                            pinningStatus = PinningStatus.FAIL,
                            pinUsed = currentState().pinValue
                        )
                    )
                    Timber.e(it)
                    handleInvalidPin()
                }
        }
    }

    private suspend fun handleInvalidPin() {
        setState { copy(invalidPin = true, pinValue = "", isKeypadEnabled = true) }
        runCatching {
            if (userRepository.needUsersSynced()) userRepository.forceSyncUsers()
        }.onFailure {
            Timber.e(it)
        }
    }
}
