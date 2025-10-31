package com.vaxcare.unifiedhub.feature.transactions.ui.returns.session

import androidx.lifecycle.ViewModel
import com.vaxcare.unifiedhub.core.model.PickupAvailability
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ReturnsSession @Inject constructor() : ViewModel() {
    // Transaction keys for backend submission
    val transactionKey: String = UUID.randomUUID().toString()
    val groupGuid: String = UUID.randomUUID().toString()

    var pickup: PickupAvailability? = null

    private val _lotState: MutableStateFlow<Map<String, LotState>> = MutableStateFlow(mapOf())
    val lotState: StateFlow<Map<String, LotState>>
        get() = _lotState.asStateFlow()
    val submittableLotState
        get() = lotState.onEach { emission ->
            emission.filter { !it.value.isDeleted }
        }

    fun populateLotState(data: Map<String, LotState>) {
        _lotState.update { data }
    }

    fun adjustCount(lotNumber: String, change: Int) {
        update(lotNumber) {
            it.copy(count = it.count + change)
        }
    }

    fun setCount(lotNumber: String, count: Int) {
        updateOrCreate(lotNumber) {
            it.copy(count = count)
        }
    }

    fun setDeleted(lotNumber: String, isDeleted: Boolean) {
        update(lotNumber) {
            it.copy(isDeleted = isDeleted)
        }
    }

    /**
     * @return Whether or not changes have been made since beginning the session.
     */
    fun containsSessionChanges(): Boolean = lotState.value.isNotEmpty()

    fun hardReset() {
        _lotState.update { mapOf() }
    }

    private fun update(lotNumber: String, mutator: (LotState) -> LotState) {
        _lotState.update { session ->
            val lotState = mutator(session[lotNumber] ?: return)
            session + mutableMapOf(lotNumber to lotState)
        }
    }

    private fun updateOrCreate(lotNumber: String, mutator: (LotState) -> LotState) {
        _lotState.update { session ->
            val countLot = mutator(session[lotNumber] ?: LotState())
            session + mutableMapOf(lotNumber to countLot)
        }
    }

    data class LotState(
        val count: Int = 1,
        val isDeleted: Boolean = false,
        val receiptKey: String = UUID.randomUUID().toString()
    )
}
