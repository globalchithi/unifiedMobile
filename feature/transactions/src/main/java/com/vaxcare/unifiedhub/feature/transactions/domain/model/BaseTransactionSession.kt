package com.vaxcare.unifiedhub.feature.transactions.domain.model

import androidx.lifecycle.ViewModel
import com.vaxcare.unifiedhub.feature.transactions.model.LotState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.util.UUID

@Deprecated("Do not use TransactionSession")
abstract class BaseTransactionSession : ViewModel(), TransactionSession {
    private val _lotState: MutableStateFlow<Map<String, LotState>> = MutableStateFlow(mapOf())
    override val lotState: StateFlow<Map<String, LotState>>
        get() = _lotState.asStateFlow()

    private val _committedLotState = mutableMapOf<String, LotState>()
    override val committedLotState: Map<String, LotState> // source of truth
        get() = _committedLotState

    private val _addedLots: MutableStateFlow<Set<Pair<Int, String>>> = MutableStateFlow(emptySet())
    override val addedLots: StateFlow<Set<Pair<Int, String>>>
        get() = _addedLots.asStateFlow()

    private val _searchedLot: MutableStateFlow<String?> = MutableStateFlow(null)
    override val searchedLot: StateFlow<String?>
        get() = _searchedLot.asStateFlow()

    override var productId: Int? = null

    override val transactionKey: String = UUID.randomUUID().toString()
    override val groupGuid: String = UUID.randomUUID().toString()

    override fun setDelta(lotNumber: String, delta: Int) {
        updateLot(lotNumber) {
            it?.copy(
                delta = delta,
            ) ?: LotState(
                delta = delta,
            )
        }
    }

    override fun setDeleted(lotNumber: String, isDeleted: Boolean,) {
        updateLot(lotNumber) {
            it?.copy(
                isDeleted = isDeleted,
            ) ?: LotState(
                isDeleted = isDeleted
            )
        }
    }

    override fun setSearchedLot(lotNumber: String) {
        _searchedLot.update { lotNumber }
    }

    override fun addLot(productId: Int?, lotNumber: String?) {
        if (productId == null || lotNumber == null) return
        _addedLots.update {
            it + setOf(productId to lotNumber)
        }
    }

    override fun containsUncommittedChanges(): Boolean {
        val currentState = _lotState.value

        // check for mismatched keys
        val keysChanged = currentState.keys
            .intersect(_committedLotState.keys)
            .size != _committedLotState.size

        if (keysChanged) return true

        // if no new entries, check existing values
        return currentState.any { (lotNumber, state) ->
            val committed = _committedLotState[lotNumber]
            state.isDeleted != committed?.isDeleted || state.delta != committed.delta
        }
    }

    override fun commitChanges() {
        _committedLotState.putAll(_lotState.value)
        resetLotState()

        productId?.let {
            confirmProduct(it)
        } ?: {
            Timber.Forest.e(
                "Changes were submitted to a TransactionSession with a null product ID. This is most likely a logical error."
            )
        }
    }

    override fun resetLotState() {
        _lotState.update { committedLotState }
    }

    override fun clearSearchedLot() {
        _searchedLot.update { null }
    }

    override fun updateLot(lotNumber: String, mutator: (LotState?) -> LotState) {
        _lotState.update { session ->
            val countLot = mutator(session[lotNumber])
            session + mutableMapOf(lotNumber to countLot).toMutableMap()
        }
    }
}
