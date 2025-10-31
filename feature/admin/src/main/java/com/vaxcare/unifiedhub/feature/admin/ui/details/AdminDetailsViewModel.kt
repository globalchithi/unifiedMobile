package com.vaxcare.unifiedhub.feature.admin.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.domain.SetupVaxHub
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDetailsViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val locationRepository: LocationRepository,
    private val setupVaxHub: SetupVaxHub,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminDetailsState())
    internal val screenUiState = _uiState
        .map(AdminDetailsState::toScreenUiState)
        .onStart { loadData() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _uiState.value.toScreenUiState()
        )
    internal val keypadUiState = _uiState
        .map(AdminDetailsState::toKeypadUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _uiState.value.toKeypadUiState()
        )

    private fun loadData() {
        viewModelScope.launch(dispatcherProvider.io) {
            val pidCidDeferred = async { locationRepository.pidCid.first() }
            val locationDeferred = async { locationRepository.getLocation().first() }
            val (pid, cid) = pidCidDeferred.await().let { (pid, cid) ->
                Pair(
                    first = pid.takeIf { it != 0L }?.toString() ?: "",
                    second = cid.takeIf { it != 0L }?.toString() ?: ""
                )
            }

            val clinicName = locationDeferred.await()?.clinicName ?: "Unknown"
            _uiState.update {
                it.copy(
                    partnerID = pid,
                    clinicID = cid,
                    clinicName = clinicName
                )
            }
        }
    }

    fun onEditPartnerID() {
        _uiState.update {
            it.copy(
                showKeypad = true,
                keypadTarget = KeypadTarget.PARTNER,
            )
        }
    }

    fun onEditClinicID() {
        _uiState.update {
            it.copy(
                showKeypad = true,
                keypadTarget = KeypadTarget.CLINIC,
            )
        }
    }

    fun onKeypadNumberClick(value: Char) {
        _uiState.update {
            if (it.keypadInput.length < 7) {
                it.copy(keypadInput = it.keypadInput + value)
            } else {
                it
            }
        }
    }

    fun onKeypadClose() {
        _uiState.update {
            it.copy(showKeypad = false, keypadInput = "", keypadTarget = null)
        }
    }

    fun onKeypadDeleteClick() {
        _uiState.update {
            it.copy(keypadInput = it.keypadInput.dropLast(1))
        }
    }

    fun onKeypadClearClick() {
        _uiState.update {
            it.copy(keypadInput = "")
        }
    }

    fun onKeypadSubmit() {
        _uiState.update { initial ->

            // apply the submitted input
            when (initial.keypadTarget) {
                KeypadTarget.PARTNER -> initial.copy(partnerID = initial.keypadInput)
                KeypadTarget.CLINIC -> initial.copy(clinicID = initial.keypadInput)
                null -> initial
            }.copy(
                keypadInput = ""
            ).let {
                // if either field is empty, change keypad target to the empty field
                when {
                    it.partnerID.isEmpty() -> it.copy(keypadTarget = KeypadTarget.PARTNER)

                    it.clinicID.isEmpty() -> it.copy(keypadTarget = KeypadTarget.CLINIC)

                    else -> it.copy(keypadTarget = null, showKeypad = false)
                }
            }
        }

        viewModelScope.launch(dispatcherProvider.io) {
            val partnerID = _uiState.value.partnerID
            val clinicID = _uiState.value.clinicID
            if (partnerID.isBlank() || clinicID.isBlank()) {
                return@launch
            }

            _uiState.update {
                it.copy(isLoading = true, isError = false)
            }

            val (valid, tabletId) = locationRepository.getCheckData(partnerID, clinicID)
            if (valid) {
                setupVaxHub(
                    pid = partnerID,
                    cid = clinicID,
                    tabletId = tabletId,
                    scope = this@launch
                ).join()
                onSyncSuccessful()
                return@launch
            }

            _uiState.update {
                it.copy(isLoading = false, isError = true, clinicName = "")
            }
        }
    }

    private suspend fun onSyncSuccessful() {
        val clinicName = locationRepository.getLocation().first()?.clinicName ?: "Unknown"
        _uiState.update {
            it.copy(isLoading = false, clinicName = clinicName)
        }
    }
}

internal enum class KeypadTarget {
    PARTNER,
    CLINIC
}

internal data class AdminDetailsState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val showKeypad: Boolean = false,
    val partnerID: String = "",
    val clinicID: String = "",
    val clinicName: String = "",
    val keypadTarget: KeypadTarget? = null,
    val keypadInput: String = "",
) {
    fun toScreenUiState() =
        ScreenUiState(
            isLoading,
            isError,
            partnerID,
            clinicID,
            clinicName,
            showKeypad,
        )

    fun toKeypadUiState(): KeypadUiState =
        KeypadUiState(
            input = keypadInput,
            isPartnerId = keypadTarget == KeypadTarget.PARTNER
        )

    data class ScreenUiState(
        val isLoading: Boolean,
        val isError: Boolean,
        val partnerID: String,
        val clinicID: String,
        val clinicName: String,
        val showKeypad: Boolean,
    )

    data class KeypadUiState(
        val input: String,
        val isPartnerId: Boolean,
    )
}
